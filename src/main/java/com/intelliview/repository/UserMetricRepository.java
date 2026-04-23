package com.intelliview.repository;

import com.intelliview.model.UserMetric;
import com.intelliview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMetricRepository extends JpaRepository<UserMetric, UUID> {
    Optional<UserMetric> findByUserAndDate(User user, LocalDate date);
    List<UserMetric> findByUserAndDateBetweenOrderByDateAsc(User user, LocalDate start, LocalDate end);

    @Query("SELECT SUM(m.problemsSolved) FROM UserMetric m WHERE m.user = :user")
    Integer getTotalProblemsSolved(@Param("user") User user);

    @Query("SELECT SUM(m.timeSpentMinutes) FROM UserMetric m WHERE m.user = :user")
    Integer getTotalTimeSpent(@Param("user") User user);
}
