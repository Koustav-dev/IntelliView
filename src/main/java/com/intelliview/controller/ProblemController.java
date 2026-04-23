package com.intelliview.controller;

import com.intelliview.dto.ApiResponse;
import com.intelliview.dto.ProblemDTO;
import com.intelliview.model.User;
import com.intelliview.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProblemDTO>>> getProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal User currentUser) {

        Page<ProblemDTO> problems = problemService.getProblems(page, size, difficulty, company, search, currentUser);
        return ResponseEntity.ok(ApiResponse.success(problems));
    }

    @GetMapping("/{slugOrId}")
    public ResponseEntity<ApiResponse<ProblemDTO>> getProblem(
            @PathVariable String slugOrId,
            @AuthenticationPrincipal User currentUser) {
        ProblemDTO problem = problemService.getProblem(slugOrId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(problem));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProblemDTO>> createProblem(@RequestBody ProblemDTO dto) {
        ProblemDTO created = problemService.createProblem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Problem created", created));
    }
}
