package com.intelliview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "problems")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(unique = true, nullable = false, length = 500)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(length = 100)
    private String category;

    @ElementCollection
    @CollectionTable(name = "problem_tags", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "problem_companies", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "company")
    @Builder.Default
    private List<String> companies = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> examples;

    @ElementCollection
    @CollectionTable(name = "problem_hints", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "hint")
    @Builder.Default
    private List<String> hints = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "solution_template", columnDefinition = "jsonb")
    private Map<String, String> solutionTemplate; // language -> template

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "optimal_solution", columnDefinition = "jsonb")
    private Map<String, String> optimalSolution;

    @Column(name = "time_complexity", length = 100)
    private String timeComplexity;

    @Column(name = "space_complexity", length = 100)
    private String spaceComplexity;

    @Column(name = "acceptance_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal acceptanceRate = BigDecimal.ZERO;

    @Column(name = "total_submissions")
    @Builder.Default
    private Integer totalSubmissions = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "premium_only")
    @Builder.Default
    private Boolean premiumOnly = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Difficulty { EASY, MEDIUM, HARD }
}
