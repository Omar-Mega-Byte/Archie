package com.archie.auth.service;

import com.archie.auth.dto.ChangePasswordRequest;
import com.archie.auth.dto.UpdateProfileRequest;
import com.archie.auth.dto.UserProfileResponse;
import com.archie.auth.entity.User;
import com.archie.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile() {
        User user = getCurrentUser();
        // Reload to get fresh data
        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User savedUser = userRepository.save(user);
        log.info("Profile updated for user: {}", savedUser.getUsername());

        return buildProfileResponse(savedUser);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Verify new password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getUsername());
    }

    @Transactional
    public void incrementUserStats(Long userId, Long generationTimeMs, Integer entityCount) {
        userRepository.incrementUserStats(userId, generationTimeMs, entityCount);
    }

    private UserProfileResponse buildProfileResponse(User user) {
        Double avgGenerationTime = null;
        if (user.getProjectsGenerated() > 0 && user.getTotalGenerationTimeMs() > 0) {
            avgGenerationTime = (double) user.getTotalGenerationTimeMs() / user.getProjectsGenerated();
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .projectsGenerated(user.getProjectsGenerated())
                .diagramsAnalyzed(user.getDiagramsAnalyzed())
                .totalGenerationTimeMs(user.getTotalGenerationTimeMs())
                .averageGenerationTimeMs(avgGenerationTime)
                .totalEntities(user.getTotalEntities())
                .build();
    }
}
