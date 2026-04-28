package com.intelliview.repository;

import com.intelliview.model.BehavioralQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BehavioralQuestionRepository extends JpaRepository<BehavioralQuestion, UUID> {

    Page<BehavioralQuestion> findByIsActiveTrue(Pageable pageable);

    Page<BehavioralQuestion> findByCategoryAndIsActiveTrue(String category, Pageable pageable);

    // Use native PostgreSQL ANY() for TEXT[] companies column
    @Query(value = "SELECT * FROM behavioral_questions WHERE is_active = true AND :company = ANY(companies)",
           nativeQuery = true)
    List<BehavioralQuestion> findByCompany(@Param("company") String company);

    @Query(value = "SELECT * FROM behavioral_questions WHERE is_active = true ORDER BY RANDOM() LIMIT :count",
           nativeQuery = true)
    List<BehavioralQuestion> findRandomQuestions(@Param("count") int count);
}
