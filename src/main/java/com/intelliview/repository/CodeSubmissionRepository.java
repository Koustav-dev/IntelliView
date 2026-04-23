package com.intelliview.repository;

import com.intelliview.model.CodeSubmission;
import com.intelliview.model.User;
import com.intelliview.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, UUID> {

    Page<CodeSubmission> findByUserOrderBySubmittedAtDesc(User user, Pageable pageable);

    List<CodeSubmission> findByUserAndProblemOrderBySubmittedAtDesc(User user, Problem problem);

    @Query("SELECT cs FROM CodeSubmission cs WHERE cs.user = :user AND cs.status = 'ACCEPTED' " +
           "GROUP BY cs.problem ORDER BY MAX(cs.submittedAt) DESC")
    List<CodeSubmission> findAcceptedSubmissionsByUser(@Param("user") User user);

    @Query("SELECT COUNT(DISTINCT cs.problem) FROM CodeSubmission cs WHERE cs.user = :user AND cs.status = 'ACCEPTED'")
    long countSolvedProblems(@Param("user") User user);

    @Query("SELECT cs.language, COUNT(cs) FROM CodeSubmission cs WHERE cs.user = :user GROUP BY cs.language")
    List<Object[]> getLanguageStats(@Param("user") User user);

    @Query("SELECT AVG(cs.codeQualityScore) FROM CodeSubmission cs WHERE cs.user = :user AND cs.codeQualityScore IS NOT NULL")
    Double getAvgCodeQuality(@Param("user") User user);

    boolean existsByUserAndProblemAndStatus(User user, Problem problem, CodeSubmission.Status status);
}
