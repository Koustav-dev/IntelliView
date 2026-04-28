package com.intelliview.service;

import com.intelliview.dto.*;
import com.intelliview.model.*;
import com.intelliview.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InterviewService {

    private final InterviewSessionRepository sessionRepo;
    private final BehavioralQuestionRepository bqRepo;
    private final BehavioralResponseRepository brRepo;
    private final ProblemRepository problemRepo;
    private final UserRepository userRepo;
    private final GroqAIService groqAIService;

    // ---- Session Management ----

    public InterviewSessionDTO startSession(User user, String sessionType, String company, String difficulty) {
        InterviewSession session = InterviewSession.builder()
                .user(user)
                .sessionType(InterviewSession.SessionType.valueOf(sessionType.toUpperCase()))
                .companyTarget(company)
                .difficulty(difficulty)
                .build();

        session = sessionRepo.save(session);
        userRepo.incrementTotalInterviews(user.getId());

        return mapToDTO(session);
    }

    public InterviewSessionDTO completeSession(User user, UUID sessionId, String feedback) {
        InterviewSession session = sessionRepo.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        session.setStatus(InterviewSession.Status.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());

        // Calculate duration
        long minutes = java.time.Duration.between(session.getStartedAt(), session.getCompletedAt()).toMinutes();
        session.setDurationMinutes((int) minutes);

        // Generate comprehensive AI feedback
        List<String> strengths = session.getStrengths();
        List<String> weaknesses = session.getImprovementAreas();
        String aiFeedback = groqAIService.generateInterviewFeedback(
                session.getSessionType().name(),
                session.getTotalScore(),
                strengths, weaknesses,
                session.getCompanyTarget()
        );
        session.setAiFeedback(aiFeedback);

        session = sessionRepo.save(session);
        return mapToDTO(session);
    }

    public InterviewSessionDTO abandonSession(User user, UUID sessionId) {
        InterviewSession session = sessionRepo.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.setStatus(InterviewSession.Status.ABANDONED);
        session = sessionRepo.save(session);
        return mapToDTO(session);
    }

    public Page<InterviewSessionDTO> getUserSessions(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sessionRepo.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToDTO);
    }

    public InterviewSessionDTO getSession(User user, UUID sessionId) {
        return sessionRepo.findByIdAndUser(sessionId, user)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    // ---- Behavioral Interview ----

    public List<Map<String, Object>> getBehavioralQuestions(String company, String category, int count) {
        List<BehavioralQuestion> questions;
        if (company != null && !company.isBlank()) {
            questions = bqRepo.findByCompany(company);
            // Fallback to random if no company-specific questions found
            if (questions.isEmpty()) {
                questions = bqRepo.findRandomQuestions(count);
            }
        } else if (category != null && !category.isBlank()) {
            questions = bqRepo.findByCategoryAndIsActiveTrue(category, PageRequest.of(0, count)).getContent();
            if (questions.isEmpty()) {
                questions = bqRepo.findRandomQuestions(count);
            }
        } else {
            questions = bqRepo.findRandomQuestions(count);
        }
        // Limit to requested count
        if (questions.size() > count) questions = questions.subList(0, count);

        return questions.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId().toString());
            map.put("question", q.getQuestion());
            map.put("category", q.getCategory());
            map.put("difficulty", q.getDifficulty());
            map.put("companies", q.getCompanies() != null ? q.getCompanies() : List.of());
            return map;
        }).collect(Collectors.toList());
    }


    public Map<String, Object> submitBehavioralResponse(User user, UUID questionId,
                                                         UUID sessionId, String responseText) {
        BehavioralQuestion question = bqRepo.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // AI analysis
        Map<String, Object> analysis = groqAIService.analyzeBehavioralResponse(
                question.getQuestion(), responseText, question.getKeywords()
        );

        BehavioralResponse response = BehavioralResponse.builder()
                .user(user)
                .question(question)
                .responseText(responseText)
                .aiFeedback(analysis)
                .build();

        // Extract scores
        extractScores(response, analysis);

        if (sessionId != null) {
            sessionRepo.findById(sessionId).ifPresent(response::setSession);
        }

        brRepo.save(response);

        return analysis;
    }

    public String generateAIBehavioralQuestion(String company, String category) {
        String prompt = String.format(
            "Generate a behavioral interview question%s%s. " +
            "Return just the question text, nothing else.",
            company != null ? " for " + company : "",
            category != null ? " about " + category : ""
        );
        return groqAIService.chat(
            "You are an experienced HR interviewer at a top tech company.",
            prompt
        );
    }

    public Map<String, Object> getMockInterviewKit(String company, String difficulty, int problemCount) {
        Map<String, Object> kit = new HashMap<>();

        // Get problems — try company-specific first, fallback to difficulty-only
        List<Problem> problems = new ArrayList<>();
        if (company != null && !company.isBlank()) {
            try {
                problems = problemRepo.findByDifficultyAndCompany(
                        difficulty.toUpperCase(), company, problemCount
                );
            } catch (Exception e) {
                log.warn("Could not fetch company-specific problems for {}: {}", company, e.getMessage());
            }
        }
        // Fallback: if no company-specific problems found, use difficulty filter
        if (problems.isEmpty()) {
            try {
                Problem.Difficulty diff = Problem.Difficulty.valueOf(difficulty.toUpperCase());
                problems = problemRepo.findByDifficultyAndIsActiveTrue(diff, PageRequest.of(0, problemCount)).getContent();
            } catch (Exception e) {
                log.warn("Fallback problem fetch failed: {}", e.getMessage());
                problems = problemRepo.findByIsActiveTrue(PageRequest.of(0, problemCount)).getContent();
            }
        }

        kit.put("codingProblems", problems.stream().map(this::mapProblemBrief).collect(Collectors.toList()));

        // Get behavioral questions
        kit.put("behavioralQuestions", getBehavioralQuestions(company, null, 3));

        // Kit metadata
        kit.put("company", company != null ? company : "General");
        kit.put("difficulty", difficulty);
        kit.put("estimatedDuration", "60-90 minutes");

        // AI-generated tips
        try {
            String tips = groqAIService.chat(
                "You are an expert technical interviewer.",
                "Give 5 specific tips for interviewing at " + (company != null ? company : "top tech companies") + " in bullet points. Be concise."
            );
            kit.put("tips", tips);
        } catch (Exception e) {
            log.warn("Could not generate AI tips: {}", e.getMessage());
            kit.put("tips", "• Practice DSA problems daily\n• Study system design fundamentals\n• Prepare STAR stories for behavioral questions\n• Review time/space complexity\n• Mock interview with peers");
        }

        return kit;
    }

    public Map<String, Object> generateStudyPlan(User user, String targetCompany, int weeks) {
        return groqAIService.generateStudyPlan(
                targetCompany,
                user.getExperienceLevel().name(),
                weeks,
                null
        );
    }

    /**
     * Direct AI analysis for AI-generated questions that have no DB record.
     */
    public Map<String, Object> analyzeResponseDirectly(String questionText, String responseText) {
        return groqAIService.analyzeBehavioralResponse(questionText, responseText, null);
    }

    private void extractScores(BehavioralResponse response, Map<String, Object> analysis) {
        try {
            if (analysis.containsKey("starScore")) {
                response.setStarScore(((Number) analysis.get("starScore")).intValue());
            }
            if (analysis.containsKey("clarityScore")) {
                response.setClarityScore(((Number) analysis.get("clarityScore")).intValue());
            }
            if (analysis.containsKey("relevanceScore")) {
                response.setRelevanceScore(((Number) analysis.get("relevanceScore")).intValue());
            }
            if (analysis.containsKey("overallScore")) {
                response.setOverallScore(((Number) analysis.get("overallScore")).intValue());
            }
        } catch (Exception e) {
            log.warn("Failed to extract scores from AI analysis");
        }
    }

    private Map<String, Object> mapProblemBrief(Problem p) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId().toString());
        map.put("title", p.getTitle());
        map.put("difficulty", p.getDifficulty().name());
        map.put("category", p.getCategory());
        map.put("slug", p.getSlug());
        return map;
    }

    private InterviewSessionDTO mapToDTO(InterviewSession s) {
        java.math.BigDecimal pct = s.getMaxScore() > 0 ?
            java.math.BigDecimal.valueOf(s.getTotalScore() * 100.0 / s.getMaxScore())
                    .setScale(1, java.math.RoundingMode.HALF_UP)
            : java.math.BigDecimal.ZERO;

        return InterviewSessionDTO.builder()
                .id(s.getId().toString())
                .sessionType(s.getSessionType().name())
                .companyTarget(s.getCompanyTarget())
                .difficulty(s.getDifficulty())
                .status(s.getStatus().name())
                .totalScore(s.getTotalScore())
                .maxScore(s.getMaxScore())
                .durationMinutes(s.getDurationMinutes())
                .startedAt(s.getStartedAt())
                .completedAt(s.getCompletedAt())
                .aiFeedback(s.getAiFeedback())
                .improvementAreas(s.getImprovementAreas())
                .strengths(s.getStrengths())
                .metadata(s.getMetadata())
                .scorePercentage(pct)
                .build();
    }
}
