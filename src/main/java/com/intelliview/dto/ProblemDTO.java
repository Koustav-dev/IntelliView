package com.intelliview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDTO {
    private String id;
    private String title;
    private String slug;
    private String description;
    private String difficulty;
    private String category;
    private List<String> tags;
    private List<String> companies;
    private String constraints;
    private List<Map<String, Object>> examples;
    private List<String> hints;
    private Map<String, String> solutionTemplate;
    private String timeComplexity;
    private String spaceComplexity;
    private double acceptanceRate;
    private int totalSubmissions;
    private boolean premiumOnly;
    private LocalDateTime createdAt;
    // User-specific fields (populated when authenticated)
    private Boolean solved;
    private String userStatus;
}
