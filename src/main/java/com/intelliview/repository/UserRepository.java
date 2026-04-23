package com.intelliview.repository;

import com.intelliview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetToken(String token);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.streakCount = u.streakCount + 1 WHERE u.id = :id")
    void incrementStreak(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.totalProblemsSolved = u.totalProblemsSolved + 1 WHERE u.id = :id")
    void incrementProblemsSolved(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.totalInterviews = u.totalInterviews + 1 WHERE u.id = :id")
    void incrementTotalInterviews(UUID id);
}
