package com.intelliview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {
    private String id;
    private String problemId;
    private String problemTitle;
    private String language;
    private String code;
    private String status;
    private Integer runtimeMs;
    private Integer memoryKb;
    private Integer testCasesPassed;
    private Integer totalTestCases;
    private String output;
    private String errorMessage;
    private Map<String, Object> aiCodeReview;
    private String timeComplexityDetected;
    private String spaceComplexityDetected;
    private Integer codeQualityScore;
    private LocalDateTime submittedAt;
}
