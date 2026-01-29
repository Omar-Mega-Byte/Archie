package com.archie.auth.repository;

import com.archie.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :userId")
    void updateLastLoginAt(@Param("userId") Long userId, @Param("lastLoginAt") LocalDateTime lastLoginAt);

    @Modifying
    @Query("UPDATE User u SET u.projectsGenerated = u.projectsGenerated + 1, " +
            "u.diagramsAnalyzed = u.diagramsAnalyzed + 1, " +
            "u.totalGenerationTimeMs = u.totalGenerationTimeMs + :generationTime, " +
            "u.totalEntities = u.totalEntities + :entityCount " +
            "WHERE u.id = :userId")
    void incrementUserStats(@Param("userId") Long userId, @Param("generationTime") Long generationTime,
            @Param("entityCount") Integer entityCount);
}
