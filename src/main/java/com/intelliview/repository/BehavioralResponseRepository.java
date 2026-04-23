package com.intelliview.repository;

import com.intelliview.model.BehavioralResponse;
import com.intelliview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BehavioralResponseRepository extends JpaRepository<BehavioralResponse, UUID> {
    List<BehavioralResponse> findByUserOrderBySubmittedAtDesc(User user);

    @Query("SELECT AVG(br.overallScore) FROM BehavioralResponse br WHERE br.user = :user AND br.overallScore IS NOT NULL")
    Double getAvgBehavioralScore(@Param("user") User user);
}
