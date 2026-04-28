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

    // Native query using PostgreSQL ANY() for TEXT[] column — JDBC parameter binding compatible
    @Query(value = "SELECT * FROM problems WHERE is_active = true AND :company = ANY(companies)",
           countQuery = "SELECT COUNT(*) FROM problems WHERE is_active = true AND :company = ANY(companies)",
           nativeQuery = true)
    Page<Problem> findByCompany(@Param("company") String company, Pageable pageable);

    // Search by title or category
    @Query(value = "SELECT * FROM problems WHERE is_active = true AND " +
           "(title ILIKE '%' || :search || '%' OR category ILIKE '%' || :search || '%' OR :search = ANY(tags))",
           countQuery = "SELECT COUNT(*) FROM problems WHERE is_active = true AND " +
           "(title ILIKE '%' || :search || '%' OR category ILIKE '%' || :search || '%' OR :search = ANY(tags))",
           nativeQuery = true)
    Page<Problem> searchProblems(@Param("search") String search, Pageable pageable);

    // Find by difficulty and company — uses ANY() for proper JDBC binding, LIMIT as int param
    @Query(value = "SELECT * FROM problems WHERE is_active = true " +
           "AND difficulty = :difficulty AND :company = ANY(companies) LIMIT :limit",
           nativeQuery = true)
    List<Problem> findByDifficultyAndCompany(
        @Param("difficulty") String difficulty,
        @Param("company") String company,
        @Param("limit") int limit);

    @Query("SELECT COUNT(p) FROM Problem p WHERE p.difficulty = :difficulty AND p.isActive = true")
    long countByDifficulty(@Param("difficulty") Problem.Difficulty difficulty);

    @Query(value = "SELECT * FROM problems WHERE is_active = true ORDER BY RANDOM() LIMIT :count",
           nativeQuery = true)
    List<Problem> findRandomProblems(@Param("count") int count);
}
