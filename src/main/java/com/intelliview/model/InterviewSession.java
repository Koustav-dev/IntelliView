package com.intelliview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "interview_sessions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Column(name = "company_target", length = 100)
    private String companyTarget;

    @Column(length = 20)
    private String difficulty;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.IN_PROGRESS;

    @Column(name = "total_score")
    @Builder.Default
    private Integer totalScore = 0;

    @Column(name = "max_score")
    @Builder.Default
    private Integer maxScore = 100;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "started_at")
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @ElementCollection
    @CollectionTable(name = "session_improvement_areas", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "area")
    @Builder.Default
    private List<String> improvementAreas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "session_strengths", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "strength")
    @Builder.Default
    private List<String> strengths = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum SessionType { TECHNICAL, BEHAVIORAL, SYSTEM_DESIGN, MIXED }
    public enum Status { IN_PROGRESS, COMPLETED, ABANDONED }
}
