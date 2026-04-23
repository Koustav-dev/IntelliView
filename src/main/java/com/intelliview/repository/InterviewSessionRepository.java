package com.intelliview.repository;

import com.intelliview.model.InterviewSession;
import com.intelliview.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {

    Page<InterviewSession> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<InterviewSession> findByUserAndStatusOrderByCreatedAtDesc(User user, InterviewSession.Status status);

    Optional<InterviewSession> findByIdAndUser(UUID id, User user);

    @Query("SELECT AVG(s.totalScore) FROM InterviewSession s WHERE s.user = :user AND s.status = 'COMPLETED'")
    Double getAverageScore(@Param("user") User user);

    @Query("SELECT s.sessionType, COUNT(s) FROM InterviewSession s WHERE s.user = :user GROUP BY s.sessionType")
    List<Object[]> getSessionTypeStats(@Param("user") User user);

    @Query("SELECT COUNT(s) FROM InterviewSession s WHERE s.user = :user AND s.status = 'COMPLETED'")
    long countCompletedByUser(@Param("user") User user);

    @Query("SELECT s FROM InterviewSession s WHERE s.user = :user AND s.status = 'COMPLETED' " +
           "ORDER BY s.completedAt DESC")
    List<InterviewSession> findRecentCompleted(@Param("user") User user, Pageable pageable);
}
