package com.intelliview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "code_submissions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CodeSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private InterviewSession session;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "runtime_ms")
    private Integer runtimeMs;

    @Column(name = "memory_kb")
    private Integer memoryKb;

    @Column(name = "test_cases_passed")
    @Builder.Default
    private Integer testCasesPassed = 0;

    @Column(name = "total_test_cases")
    @Builder.Default
    private Integer totalTestCases = 0;

    @Column(columnDefinition = "TEXT")
    private String output;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_code_review", columnDefinition = "jsonb")
    private Map<String, Object> aiCodeReview;

    @Column(name = "time_complexity_detected", length = 100)
    private String timeComplexityDetected;

    @Column(name = "space_complexity_detected", length = 100)
    private String spaceComplexityDetected;

    @Column(name = "code_quality_score")
    private Integer codeQualityScore;

    @Column(name = "submitted_at")
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    public enum Status {
        PENDING, RUNNING, ACCEPTED, WRONG_ANSWER,
        TIME_LIMIT_EXCEEDED, MEMORY_LIMIT_EXCEEDED,
        RUNTIME_ERROR, COMPILE_ERROR
    }
}
