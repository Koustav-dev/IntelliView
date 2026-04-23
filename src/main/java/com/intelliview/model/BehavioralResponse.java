package com.intelliview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "behavioral_responses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BehavioralResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private BehavioralQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private InterviewSession session;

    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;

    @Column(name = "audio_url")
    private String audioUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_feedback", columnDefinition = "jsonb")
    private Map<String, Object> aiFeedback;

    @Column(name = "star_score")
    private Integer starScore;

    @Column(name = "clarity_score")
    private Integer clarityScore;

    @Column(name = "relevance_score")
    private Integer relevanceScore;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "submitted_at")
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();
}
