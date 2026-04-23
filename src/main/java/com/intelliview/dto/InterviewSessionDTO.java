package com.intelliview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSessionDTO {
    private String id;
    private String sessionType;
    private String companyTarget;
    private String difficulty;
    private String status;
    private Integer totalScore;
    private Integer maxScore;
    private Integer durationMinutes;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String aiFeedback;
    private List<String> improvementAreas;
    private List<String> strengths;
    private Map<String, Object> metadata;
    private BigDecimal scorePercentage;
}
