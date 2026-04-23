package com.intelliview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "achievements")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String icon;

    @Column(name = "condition_type", length = 100)
    private String conditionType;

    @Column(name = "condition_value")
    private Integer conditionValue;

    @Column(name = "points")
    @Builder.Default
    private Integer points = 0;

    @Column(length = 50)
    @Builder.Default
    private String rarity = "COMMON";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
