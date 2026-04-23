package com.intelliview.controller;

import com.intelliview.dto.ApiResponse;
import com.intelliview.service.GroqAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.intelliview.model.User;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final GroqAIService groqAIService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<String>> chat(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        String systemPrompt = body.getOrDefault("systemPrompt",
                "You are IntelliView AI, a helpful assistant for coding interview preparation.");
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Message is required"));
        }
        String response = groqAIService.chat(systemPrompt, message);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/generate-problem")
    public ResponseEntity<ApiResponse<String>> generateProblem(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        String problem = groqAIService.generateProblem(
                body.getOrDefault("difficulty", "MEDIUM"),
                body.getOrDefault("topic", "Arrays"),
                body.get("company")
        );
        return ResponseEntity.ok(ApiResponse.success(problem));
    }

    @PostMapping("/study-roadmap")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudyRoadmap(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> plan = groqAIService.generateStudyPlan(
                (String) body.get("targetCompany"),
                user.getExperienceLevel().name(),
                body.get("weeks") != null ? ((Number) body.get("weeks")).intValue() : 4,
                null
        );
        return ResponseEntity.ok(ApiResponse.success(plan));
    }
}
