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

    @ElementCollection
    @CollectionTable(name = "bq_companies", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "company")
    @Builder.Default
    private List<String> companies = new ArrayList<>();

    @Column(length = 20)
    @Builder.Default
    private String difficulty = "MEDIUM";

    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer;

    @ElementCollection
    @CollectionTable(name = "bq_keywords", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "keyword")
    @Builder.Default
    private List<String> keywords = new ArrayList<>();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
