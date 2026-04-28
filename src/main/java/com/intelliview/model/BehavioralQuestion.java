package com.intelliview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "behavioral_questions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BehavioralQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(length = 100)
    private String category;

    // Native PostgreSQL TEXT[] — matches the schema directly
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "companies", columnDefinition = "text[]")
    @Builder.Default
    private List<String> companies = new ArrayList<>();

    @Column(length = 20)
    @Builder.Default
    private String difficulty = "MEDIUM";

    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "keywords", columnDefinition = "text[]")
    @Builder.Default
    private List<String> keywords = new ArrayList<>();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
