package com.intelliview.controller;

import com.intelliview.dto.ApiResponse;
import com.intelliview.dto.SubmissionDTO;
import com.intelliview.dto.SubmitCodeRequest;
import com.intelliview.model.User;
import com.intelliview.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionDTO>> submit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubmitCodeRequest request) {
        SubmissionDTO result = submissionService.submitCode(user, request);
        return ResponseEntity.ok(ApiResponse.success("Code submitted successfully", result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SubmissionDTO>>> getMySubmissions(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getUserSubmissions(user, page, size)));
    }

    @GetMapping("/problem/{problemId}")
    public ResponseEntity<ApiResponse<List<SubmissionDTO>>> getProblemSubmissions(
            @AuthenticationPrincipal User user,
            @PathVariable UUID problemId) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getProblemSubmissions(user, problemId)));
    }

    @PostMapping("/run")
    public ResponseEntity<ApiResponse<Map<String, Object>>> runTestCase(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        Map<String, Object> result = submissionService.runTestCase(
                user,
                UUID.fromString(body.get("problemId")),
                body.get("code"),
                body.get("language"),
                body.get("input")
        );
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/hint")
    public ResponseEntity<ApiResponse<String>> getHint(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        String hint = submissionService.getAIHint(
                user,
                UUID.fromString((String) body.get("problemId")),
                (String) body.get("code"),
                (String) body.get("language"),
                body.get("hintLevel") != null ? ((Number) body.get("hintLevel")).intValue() : 1
        );
        return ResponseEntity.ok(ApiResponse.success(hint));
    }

    @PostMapping("/explain")
    public ResponseEntity<ApiResponse<String>> explainSolution(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        String explanation = submissionService.explainSolution(
                user,
                UUID.fromString(body.get("problemId")),
                body.get("code"),
                body.get("language")
        );
        return ResponseEntity.ok(ApiResponse.success(explanation));
    }
}
