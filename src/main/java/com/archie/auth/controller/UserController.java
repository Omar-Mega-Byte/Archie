package com.archie.auth.controller;

import com.archie.auth.dto.ChangePasswordRequest;
import com.archie.auth.dto.UpdateProfileRequest;
import com.archie.auth.dto.UserProfileResponse;
import com.archie.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile and dashboard APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Get profile of the authenticated user")
    public ResponseEntity<UserProfileResponse> getProfile() {
        UserProfileResponse profile = userService.getUserProfile();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update profile information")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse profile = userService.updateProfile(request);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard data", description = "Get user statistics and dashboard information")
    public ResponseEntity<UserProfileResponse> getDashboard() {
        UserProfileResponse profile = userService.getUserProfile();
        return ResponseEntity.ok(profile);
    }
}
