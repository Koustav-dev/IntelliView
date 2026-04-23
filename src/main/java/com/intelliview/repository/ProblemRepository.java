package com.intelliview.repository;

import com.intelliview.model.Problem;
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
public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    Optional<Problem> findBySlug(String slug);

    Page<Problem> findByIsActiveTrue(Pageable pageable);

    Page<Problem> findByDifficultyAndIsActiveTrue(Problem.Difficulty difficulty, Pageable pageable);

    Page<Problem> findByCategoryAndIsActiveTrue(String category, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE p.isActive = true AND :company MEMBER OF p.companies")
    Page<Problem> findByCompany(@Param("company") String company, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE p.isActive = true AND " +
           "(:tag MEMBER OF p.tags OR p.title LIKE %:search% OR p.category LIKE %:search%)")
    Page<Problem> searchProblems(@Param("search") String search, @Param("tag") String tag, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE p.isActive = true AND p.difficulty = :difficulty " +
           "AND :company MEMBER OF p.companies")
    List<Problem> findByDifficultyAndCompany(
        @Param("difficulty") Problem.Difficulty difficulty,
        @Param("company") String company,
        Pageable pageable);

    @Query("SELECT COUNT(p) FROM Problem p WHERE p.difficulty = :difficulty AND p.isActive = true")
    long countByDifficulty(@Param("difficulty") Problem.Difficulty difficulty);

    @Query(value = "SELECT * FROM problems WHERE is_active = true ORDER BY RANDOM() LIMIT :count",
           nativeQuery = true)
    List<Problem> findRandomProblems(@Param("count") int count);
}
