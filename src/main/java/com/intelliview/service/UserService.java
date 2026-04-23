package com.intelliview.service;

import com.intelliview.dto.*;
import com.intelliview.model.*;
import com.intelliview.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepo;
    private final CodeSubmissionRepository submissionRepo;
    private final InterviewSessionRepository sessionRepo;
    private final BehavioralResponseRepository brRepo;
    private final UserMetricRepository metricRepo;

    public UserStatsDTO getUserStats(User user) {
        // Submission stats
        long totalSolved = submissionRepo.countSolvedProblems(user);
        Double avgQuality = submissionRepo.getAvgCodeQuality(user);
        List<Object[]> langStats = submissionRepo.getLanguageStats(user);

        // Session stats
        long completedSessions = sessionRepo.countCompletedByUser(user);
        Double avgScore = sessionRepo.getAverageScore(user);
        List<Object[]> sessionTypeStats = sessionRepo.getSessionTypeStats(user);

        // Behavioral stats
        Double avgBehavioral = brRepo.getAvgBehavioralScore(user);

        // Weekly progress (last 7 days)
        LocalDate today = LocalDate.now();
        List<UserMetric> weekMetrics = metricRepo.findByUserAndDateBetweenOrderByDateAsc(
                user, today.minusDays(6), today
        );

        // Monthly progress
        List<UserMetric> monthMetrics = metricRepo.findByUserAndDateBetweenOrderByDateAsc(
                user, today.minusDays(29), today
        );

        // Recent activity
        List<SubmissionDTO> recentSubs = submissionRepo
                .findByUserOrderBySubmittedAtDesc(user, PageRequest.of(0, 5))
                .getContent().stream()
                .map(this::mapSubmission)
                .collect(Collectors.toList());

        List<InterviewSessionDTO> recentSessions = sessionRepo
                .findRecentCompleted(user, PageRequest.of(0, 5))
                .stream()
                .map(this::mapSession)
                .collect(Collectors.toList());

        return UserStatsDTO.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .experienceLevel(user.getExperienceLevel().name())
                .streakCount(user.getStreakCount())
                .totalInterviews(user.getTotalInterviews())
                .totalProblemsSolved(user.getTotalProblemsSolved())
                .avgInterviewScore(avgScore)
                .avgCodeQuality(avgQuality)
                .avgBehavioralScore(avgBehavioral)
                .weeklyProgress(mapMetrics(weekMetrics))
                .monthlyProgress(mapMetrics(monthMetrics))
                .languageStats(mapLangStats(langStats))
                .sessionTypeStats(mapSessionTypeStats(sessionTypeStats))
                .recentSubmissions(recentSubs)
                .recentSessions(recentSessions)
                .build();
    }

    @Transactional
    public User updateProfile(User user, Map<String, Object> updates) {
        if (updates.containsKey("fullName")) user.setFullName((String) updates.get("fullName"));
        if (updates.containsKey("bio")) user.setBio((String) updates.get("bio"));
        if (updates.containsKey("githubUrl")) user.setGithubUrl((String) updates.get("githubUrl"));
        if (updates.containsKey("linkedinUrl")) user.setLinkedinUrl((String) updates.get("linkedinUrl"));
        if (updates.containsKey("avatarUrl")) user.setAvatarUrl((String) updates.get("avatarUrl"));
        if (updates.containsKey("experienceLevel")) {
            try {
                user.setExperienceLevel(User.ExperienceLevel.valueOf(
                        updates.get("experienceLevel").toString().toUpperCase()
                ));
            } catch (Exception ignored) {}
        }
        if (updates.containsKey("targetCompanies")) {
            Object tc = updates.get("targetCompanies");
            if (tc instanceof List<?> list) {
                user.setTargetCompanies(list.stream().map(Object::toString).collect(Collectors.toList()));
            }
        }
        return userRepo.save(user);
    }

    @Transactional
    public void changePassword(User user, String oldPassword, String newPassword,
                               org.springframework.security.crypto.password.PasswordEncoder encoder) {
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
    }

    @Cacheable(value = "leaderboard", key = "#period")
    public List<Map<String, Object>> getLeaderboard(String period) {
        // Top users by problems solved
        Pageable top20 = PageRequest.of(0, 20, Sort.by("totalProblemsSolved").descending());
        return userRepo.findAll(top20).getContent().stream()
                .map(u -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("userId", u.getId().toString());
                    entry.put("username", u.getUsername());
                    entry.put("fullName", u.getFullName());
                    entry.put("avatarUrl", u.getAvatarUrl());
                    entry.put("problemsSolved", u.getTotalProblemsSolved());
                    entry.put("totalInterviews", u.getTotalInterviews());
                    entry.put("streakCount", u.getStreakCount());
                    entry.put("experienceLevel", u.getExperienceLevel().name());
                    return entry;
                })
                .collect(Collectors.toList());
    }

    private List<UserStatsDTO.DailyProgress> mapMetrics(List<UserMetric> metrics) {
        return metrics.stream().map(m -> UserStatsDTO.DailyProgress.builder()
                .date(m.getDate().format(DateTimeFormatter.ISO_DATE))
                .problemsSolved(m.getProblemsSolved())
                .interviewsDone(m.getInterviewsCompleted())
                .avgScore(m.getAvgScore())
                .timeSpentMinutes(m.getTimeSpentMinutes())
                .build()).collect(Collectors.toList());
    }

    private Map<String, Long> mapLangStats(List<Object[]> raw) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : raw) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    private Map<String, Long> mapSessionTypeStats(List<Object[]> raw) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : raw) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return result;
    }

    private SubmissionDTO mapSubmission(CodeSubmission s) {
        return SubmissionDTO.builder()
                .id(s.getId().toString())
                .problemId(s.getProblem().getId().toString())
                .problemTitle(s.getProblem().getTitle())
                .language(s.getLanguage())
                .status(s.getStatus().name())
                .runtimeMs(s.getRuntimeMs())
                .codeQualityScore(s.getCodeQualityScore())
                .submittedAt(s.getSubmittedAt())
                .build();
    }

    private InterviewSessionDTO mapSession(InterviewSession s) {
        return InterviewSessionDTO.builder()
                .id(s.getId().toString())
                .sessionType(s.getSessionType().name())
                .companyTarget(s.getCompanyTarget())
                .status(s.getStatus().name())
                .totalScore(s.getTotalScore())
                .maxScore(s.getMaxScore())
                .durationMinutes(s.getDurationMinutes())
                .startedAt(s.getStartedAt())
                .completedAt(s.getCompletedAt())
                .build();
    }
}
