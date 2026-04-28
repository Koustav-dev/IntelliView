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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmissionService {

    private final CodeSubmissionRepository submissionRepo;
    private final ProblemRepository problemRepo;
    private final InterviewSessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final UserMetricRepository metricRepo;
    private final GroqAIService groqAIService;
    private final CodeExecutionService codeExecutionService;

    public SubmissionDTO submitCode(User user, SubmitCodeRequest request) {
        Problem problem = problemRepo.findById(request.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));

        // Create submission record
        CodeSubmission submission = CodeSubmission.builder()
                .user(user)
                .problem(problem)
                .language(request.getLanguage())
                .code(request.getCode())
                .status(CodeSubmission.Status.RUNNING)
                .build();

        if (request.getSessionId() != null) {
            sessionRepo.findById(request.getSessionId()).ifPresent(submission::setSession);
        }

        submission = submissionRepo.save(submission);

        // Execute code
        CodeExecutionService.ExecutionResult result = codeExecutionService.execute(
                request.getCode(), request.getLanguage(), problem.getId().toString()
        );

        // AI code review (async in production, sync here for simplicity)
        Map<String, Object> aiReview = groqAIService.analyzeCode(
                request.getCode(), request.getLanguage(),
                problem.getTitle(), problem.getDescription()
        );

        // Determine if code passed all test cases via AI review
        boolean allPassed = false;
        if (aiReview.containsKey("allTestCasesPassed") && aiReview.get("allTestCasesPassed") instanceof Boolean) {
            allPassed = (Boolean) aiReview.get("allTestCasesPassed");
        }

        // Check if this is first acceptance BEFORE updating status
        boolean firstAccepted = allPassed &&
                !submissionRepo.existsByUserAndProblemAndStatus(user, problem, CodeSubmission.Status.ACCEPTED);

        submission.setStatus(allPassed ? CodeSubmission.Status.ACCEPTED : CodeSubmission.Status.WRONG_ANSWER);
        submission.setRuntimeMs(result.runtimeMs());
        submission.setMemoryKb(result.memoryKb());
        submission.setTestCasesPassed(allPassed ? result.totalTestCases() : 0);
        submission.setTotalTestCases(result.totalTestCases());
        submission.setOutput(result.output());
        submission.setErrorMessage(allPassed ? null : "Code failed some test cases or constraints according to AI review.");
        submission.setAiCodeReview(aiReview);


        // Extract AI analysis fields
        if (aiReview.containsKey("timeComplexity")) {
            submission.setTimeComplexityDetected((String) aiReview.get("timeComplexity"));
        }
        if (aiReview.containsKey("spaceComplexity")) {
            submission.setSpaceComplexityDetected((String) aiReview.get("spaceComplexity"));
        }
        if (aiReview.containsKey("codeQualityScore")) {
            Object score = aiReview.get("codeQualityScore");
            if (score instanceof Number) {
                submission.setCodeQualityScore(((Number) score).intValue());
            }
        }

        submission = submissionRepo.save(submission);

        // Update user stats if this is the first accepted submission for this problem
        if (submission.getStatus() == CodeSubmission.Status.ACCEPTED && firstAccepted) {
            userRepo.incrementProblemsSolved(user.getId());
            updateDailyMetrics(user, problem.getDifficulty());
            updateStreak(user);
        }


        // Update problem stats
        updateProblemStats(problem, submission);

        return mapToDTO(submission);
    }

    public Page<SubmissionDTO> getUserSubmissions(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        return submissionRepo.findByUserOrderBySubmittedAtDesc(user, pageable)
                .map(this::mapToDTO);
    }

    public List<SubmissionDTO> getProblemSubmissions(User user, UUID problemId) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));
        return submissionRepo.findByUserAndProblemOrderBySubmittedAtDesc(user, problem)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Map<String, Object> runTestCase(User user, UUID problemId, String code, String language, String input) {
        return codeExecutionService.runCustomTestCase(code, language, input);
    }

    public String getAIHint(User user, UUID problemId, String currentCode, String language, int hintLevel) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));
        return groqAIService.generateHint(
                problem.getTitle(), problem.getDescription(), currentCode, language, hintLevel
        );
    }

    public String explainSolution(User user, UUID problemId, String code, String language) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));
        return groqAIService.explainSolution(problem.getTitle(), code, language);
    }

    private void updateDailyMetrics(User user, Problem.Difficulty difficulty) {
        LocalDate today = LocalDate.now();
        UserMetric metric = metricRepo.findByUserAndDate(user, today)
                .orElse(UserMetric.builder().user(user).date(today).build());

        metric.setProblemsSolved(metric.getProblemsSolved() + 1);
        Map<String, Integer> diffs = metric.getDifficultiesSolved() != null ?
                new HashMap<>(metric.getDifficultiesSolved()) : new HashMap<>();
        diffs.merge(difficulty.name(), 1, Integer::sum);
        metric.setDifficultiesSolved(diffs);
        metricRepo.save(metric);
    }

    private void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastActive = user.getLastActiveDate();
        if (lastActive == null || lastActive.isBefore(today.minusDays(1))) {
            // Reset or start streak
            if (lastActive != null && lastActive.isBefore(today.minusDays(1))) {
                // No need to reset in entity, just don't increment
            }
        }
        // Streak incremented via repository
        userRepo.incrementStreak(user.getId());
    }

    private void updateProblemStats(Problem problem, CodeSubmission submission) {
        problem.setTotalSubmissions(problem.getTotalSubmissions() + 1);
        // Acceptance rate update simplified
        problemRepo.save(problem);
    }

    private SubmissionDTO mapToDTO(CodeSubmission s) {
        return SubmissionDTO.builder()
                .id(s.getId().toString())
                .problemId(s.getProblem().getId().toString())
                .problemTitle(s.getProblem().getTitle())
                .language(s.getLanguage())
                .code(s.getCode())
                .status(s.getStatus().name())
                .runtimeMs(s.getRuntimeMs())
                .memoryKb(s.getMemoryKb())
                .testCasesPassed(s.getTestCasesPassed())
                .totalTestCases(s.getTotalTestCases())
                .output(s.getOutput())
                .errorMessage(s.getErrorMessage())
                .aiCodeReview(s.getAiCodeReview())
                .timeComplexityDetected(s.getTimeComplexityDetected())
                .spaceComplexityDetected(s.getSpaceComplexityDetected())
                .codeQualityScore(s.getCodeQualityScore())
                .submittedAt(s.getSubmittedAt())
                .build();
    }
}
