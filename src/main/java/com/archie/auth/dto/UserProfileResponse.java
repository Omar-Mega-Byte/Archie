package com.archie.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    // Statistics
    private Integer projectsGenerated;
    private Integer diagramsAnalyzed;
    private Long totalGenerationTimeMs;
    private Double averageGenerationTimeMs;
    private Integer totalEntities;
}
