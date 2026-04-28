package com.intelliview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "company_patterns")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CompanyPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "interview_rounds", columnDefinition = "jsonb")
    private List<Map<String, Object>> interviewRounds;

    // Native PostgreSQL TEXT[] arrays — matches schema
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "preferred_languages", columnDefinition = "text[]")
    @Builder.Default
    private List<String> preferredLanguages = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "focus_areas", columnDefinition = "text[]")
    @Builder.Default
    private List<String> focusAreas = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "difficulty_distribution", columnDefinition = "jsonb")
    private Map<String, Integer> difficultyDistribution;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tips", columnDefinition = "text[]")
    @Builder.Default
    private List<String> tips = new ArrayList<>();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
