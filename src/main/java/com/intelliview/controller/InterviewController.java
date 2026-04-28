package com.intelliview.controller;

import com.intelliview.dto.ApiResponse;
import com.intelliview.dto.InterviewSessionDTO;
import com.intelliview.model.User;
import com.intelliview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    // ---- Session Endpoints ----
    @PostMapping("/sessions/start")
    public ResponseEntity<ApiResponse<InterviewSessionDTO>> startSession(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        InterviewSessionDTO session = interviewService.startSession(
                user,
                body.getOrDefault("sessionType", "TECHNICAL"),
                body.get("company"),
                body.getOrDefault("difficulty", "MEDIUM")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Session started", session));
    }

    @PostMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<ApiResponse<InterviewSessionDTO>> completeSession(
            @AuthenticationPrincipal User user,
            @PathVariable UUID sessionId,
            @RequestBody(required = false) Map<String, String> body) {
        String feedback = body != null ? body.get("feedback") : null;
        InterviewSessionDTO session = interviewService.completeSession(user, sessionId, feedback);
        return ResponseEntity.ok(ApiResponse.success("Session completed", session));
    }

    @PostMapping("/sessions/{sessionId}/abandon")
    public ResponseEntity<ApiResponse<InterviewSessionDTO>> abandonSession(
            @AuthenticationPrincipal User user,
            @PathVariable UUID sessionId) {
        InterviewSessionDTO session = interviewService.abandonSession(user, sessionId);
        return ResponseEntity.ok(ApiResponse.success("Session abandoned", session));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<Page<InterviewSessionDTO>>> getMySessions(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.getUserSessions(user, page, size)));
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<InterviewSessionDTO>> getSession(
            @AuthenticationPrincipal User user,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.getSession(user, sessionId)));
    }

    // ---- Behavioral Endpoints ----
    @GetMapping("/behavioral/questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBehavioralQuestions(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(ApiResponse.success(
                interviewService.getBehavioralQuestions(company, category, count)
        ));
    }

    @PostMapping("/behavioral/submit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitBehavioral(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        String questionIdStr = (String) body.get("questionId");
        String questionText = (String) body.get("questionText"); // for AI-generated questions
        UUID sessionId = body.get("sessionId") != null ? UUID.fromString((String) body.get("sessionId")) : null;
        String response = (String) body.get("response");

        Map<String, Object> result;
        // Handle AI-generated questions that have no DB record
        boolean looksLikeUUID = questionIdStr != null &&
            questionIdStr.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
        if (looksLikeUUID) {
            UUID questionId = UUID.fromString(questionIdStr);
            result = interviewService.submitBehavioralResponse(user, questionId, sessionId, response);
        } else {
            // AI-generated question — analyze directly without DB lookup
            result = interviewService.analyzeResponseDirectly(questionText != null ? questionText : "Behavioral question", response);
        }
        return ResponseEntity.ok(ApiResponse.success("Response analyzed", result));
    }

    @PostMapping("/behavioral/generate-question")
    public ResponseEntity<ApiResponse<String>> generateQuestion(
            @RequestBody(required = false) Map<String, String> body) {
        String company = body != null ? body.get("company") : null;
        String category = body != null ? body.get("category") : null;
        String question = interviewService.generateAIBehavioralQuestion(company, category);
        return ResponseEntity.ok(ApiResponse.success(question));
    }

    // ---- Mock Interview Kit ----
    @GetMapping("/mock-kit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMockKit(
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "MEDIUM") String difficulty,
            @RequestParam(defaultValue = "3") int problemCount) {
        Map<String, Object> kit = interviewService.getMockInterviewKit(company, difficulty, problemCount);
        return ResponseEntity.ok(ApiResponse.success(kit));
    }

    // ---- Study Plan ----
    @PostMapping("/study-plan")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudyPlan(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        String targetCompany = (String) body.get("targetCompany");
        int weeks = body.get("weeks") != null ? ((Number) body.get("weeks")).intValue() : 4;
        Map<String, Object> plan = interviewService.generateStudyPlan(user, targetCompany, weeks);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }
}
