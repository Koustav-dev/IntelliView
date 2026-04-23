package com.intelliview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private String userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String experienceLevel;
    private int streakCount;
    private int totalInterviews;
    private int totalProblemsSolved;

    // Detailed stats
    private long easySolved;
    private long mediumSolved;
    private long hardSolved;
    private Double avgInterviewScore;
    private Double avgCodeQuality;
    private Double avgBehavioralScore;

    // Progress data (7/30 day)
    private List<DailyProgress> weeklyProgress;
    private List<DailyProgress> monthlyProgress;

    // Language distribution
    private Map<String, Long> languageStats;

    // Session type breakdown
    private Map<String, Long> sessionTypeStats;

    // Recent activity
    private List<SubmissionDTO> recentSubmissions;
    private List<InterviewSessionDTO> recentSessions;

    // Achievements
    private List<AchievementDTO> achievements;

    // Rank / leaderboard
    private Integer globalRank;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyProgress {
        private String date;
        private int problemsSolved;
        private int interviewsDone;
        private BigDecimal avgScore;
        private int timeSpentMinutes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AchievementDTO {
        private String name;
        private String description;
        private String icon;
        private String rarity;
        private int points;
        private String earnedAt;
    }
}
