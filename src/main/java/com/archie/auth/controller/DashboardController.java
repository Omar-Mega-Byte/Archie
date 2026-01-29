package com.archie.auth.controller;

import com.archie.auth.dto.UserProfileResponse;
import com.archie.auth.entity.User;
import com.archie.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "User dashboard and analytics APIs")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get dashboard overview", description = "Get comprehensive dashboard data including statistics")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        UserProfileResponse profile = userService.getUserProfile();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("user", profile);
        dashboard.put("stats", buildStats(profile));
        dashboard.put("recentActivity", buildRecentActivity());
        dashboard.put("quickActions", buildQuickActions());

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Get detailed user statistics and metrics")
    public ResponseEntity<Map<String, Object>> getStats() {
        UserProfileResponse profile = userService.getUserProfile();
        return ResponseEntity.ok(buildStats(profile));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get analytics data", description = "Get analytics data for charts and visualizations")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        UserProfileResponse profile = userService.getUserProfile();

        Map<String, Object> analytics = new HashMap<>();

        // Summary stats
        analytics.put("totalProjects", profile.getProjectsGenerated());
        analytics.put("totalDiagrams", profile.getDiagramsAnalyzed());
        analytics.put("avgGenerationTime", profile.getAverageGenerationTimeMs());
        analytics.put("totalGenerationTime", profile.getTotalGenerationTimeMs());

        // Weekly activity (mock data - in real app, this would come from activity logs)
        analytics.put("weeklyActivity", buildWeeklyActivity());

        // Project distribution by database type (mock data)
        analytics.put("databaseDistribution", buildDatabaseDistribution());

        // Recent generations trend (mock data)
        analytics.put("generationTrend", buildGenerationTrend());

        return ResponseEntity.ok(analytics);
    }

    private Map<String, Object> buildStats(UserProfileResponse profile) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("projectsGenerated", profile.getProjectsGenerated());
        stats.put("diagramsAnalyzed", profile.getDiagramsAnalyzed());
        stats.put("totalGenerationTimeMs", profile.getTotalGenerationTimeMs());
        stats.put("averageGenerationTimeMs", profile.getAverageGenerationTimeMs());
        stats.put("totalEntities", profile.getTotalEntities());
        stats.put("memberSince", profile.getCreatedAt());
        stats.put("lastActive", profile.getLastLoginAt());

        // Calculate productivity score (example algorithm)
        int productivityScore = calculateProductivityScore(profile);
        stats.put("productivityScore", productivityScore);

        return stats;
    }

    private int calculateProductivityScore(UserProfileResponse profile) {
        int score = 0;

        // Base score for activity
        if (profile.getProjectsGenerated() > 0) {
            score += Math.min(profile.getProjectsGenerated() * 10, 50);
        }

        // Bonus for efficiency (lower avg time is better)
        if (profile.getAverageGenerationTimeMs() != null && profile.getAverageGenerationTimeMs() < 30000) {
            score += 25;
        } else if (profile.getAverageGenerationTimeMs() != null && profile.getAverageGenerationTimeMs() < 60000) {
            score += 15;
        }

        // Bonus for regular usage
        if (profile.getLastLoginAt() != null &&
                profile.getLastLoginAt().isAfter(LocalDateTime.now().minusDays(7))) {
            score += 25;
        }

        return Math.min(score, 100);
    }

    private List<Map<String, Object>> buildRecentActivity() {
        // In a real app, this would fetch from an activity log table
        return List.of(
                Map.of(
                        "type", "project_generated",
                        "description", "Generated Spring Boot project",
                        "timestamp", LocalDateTime.now().minusHours(2)),
                Map.of(
                        "type", "login",
                        "description", "Logged in",
                        "timestamp", LocalDateTime.now().minusDays(1)));
    }

    private List<Map<String, Object>> buildQuickActions() {
        return List.of(
                Map.of(
                        "id", "new_project",
                        "title", "Generate New Project",
                        "description", "Upload a diagram to generate a new Spring Boot project",
                        "icon", "plus"),
                Map.of(
                        "id", "view_docs",
                        "title", "View Documentation",
                        "description", "Learn how to create better diagrams",
                        "icon", "book"),
                Map.of(
                        "id", "api_docs",
                        "title", "API Documentation",
                        "description", "Explore the REST API",
                        "icon", "code"));
    }

    private List<Map<String, Object>> buildWeeklyActivity() {
        // Mock weekly activity data
        return List.of(
                Map.of("day", "Mon", "projects", 2),
                Map.of("day", "Tue", "projects", 1),
                Map.of("day", "Wed", "projects", 3),
                Map.of("day", "Thu", "projects", 0),
                Map.of("day", "Fri", "projects", 2),
                Map.of("day", "Sat", "projects", 1),
                Map.of("day", "Sun", "projects", 0));
    }

    private List<Map<String, Object>> buildDatabaseDistribution() {
        // Mock database distribution data
        return List.of(
                Map.of("name", "H2", "value", 45),
                Map.of("name", "PostgreSQL", "value", 30),
                Map.of("name", "MySQL", "value", 20),
                Map.of("name", "MongoDB", "value", 5));
    }

    private List<Map<String, Object>> buildGenerationTrend() {
        // Mock generation trend for last 30 days
        return List.of(
                Map.of("date", "2026-01-01", "count", 3),
                Map.of("date", "2026-01-08", "count", 5),
                Map.of("date", "2026-01-15", "count", 4),
                Map.of("date", "2026-01-22", "count", 7),
                Map.of("date", "2026-01-29", "count", 6));
    }
}
