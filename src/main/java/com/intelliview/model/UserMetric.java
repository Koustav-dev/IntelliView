package com.intelliview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "user_metrics",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "problems_solved")
    @Builder.Default
    private Integer problemsSolved = 0;

    @Column(name = "interviews_completed")
    @Builder.Default
    private Integer interviewsCompleted = 0;

    @Column(name = "avg_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal avgScore = BigDecimal.ZERO;

    @Column(name = "time_spent_minutes")
    @Builder.Default
    private Integer timeSpentMinutes = 0;

    // Native PostgreSQL TEXT[] — matches schema (languages_used TEXT[])
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "languages_used", columnDefinition = "text[]")
    @Builder.Default
    private List<String> languagesUsed = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "difficulties_solved", columnDefinition = "jsonb")
    private Map<String, Integer> difficultiesSolved;
}
