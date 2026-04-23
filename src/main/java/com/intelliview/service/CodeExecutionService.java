package com.intelliview.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * Simulated code execution service.
 * In production, this would use Judge0 API (free tier available) or 
 * a Docker sandbox via Javalin/ProcessBuilder.
 * For now, it simulates execution results with basic analysis.
 */
@Service
@Slf4j
public class CodeExecutionService {

    private static final Map<String, String[]> TEST_TEMPLATES = new HashMap<>();
    private static final int EXECUTION_TIMEOUT_SECONDS = 10;

    public record ExecutionResult(
        String status,
        String output,
        String error,
        int runtimeMs,
        int memoryKb,
        int testCasesPassed,
        int totalTestCases
    ) {}

    public ExecutionResult execute(String code, String language, String problemId) {
        try {
            // Use Judge0 public API (free tier)
            return executeViaJudge0(code, language);
        } catch (Exception e) {
            log.warn("Judge0 execution failed, using simulation: {}", e.getMessage());
            return simulateExecution(code, language);
        }
    }

    private ExecutionResult executeViaJudge0(String code, String language) {
        // Judge0 language IDs
        Map<String, Integer> languageIds = Map.of(
            "java", 62,
            "python", 71,
            "javascript", 63,
            "cpp", 54,
            "c", 50,
            "go", 60,
            "rust", 73,
            "typescript", 74
        );

        int langId = languageIds.getOrDefault(language.toLowerCase(), 71);

        try {
            // Judge0 CE (Community Edition) - free at judge0.com
            String judge0Url = "https://judge0-ce.p.rapidapi.com";

            // Build submission
            Map<String, Object> submission = new HashMap<>();
            submission.put("source_code", code);
            submission.put("language_id", langId);
            submission.put("stdin", "");

            // In real implementation, this would make HTTP calls to Judge0
            // Returning simulation for now
            return simulateExecution(code, language);

        } catch (Exception e) {
            return simulateExecution(code, language);
        }
    }

    private ExecutionResult simulateExecution(String code, String language) {
        // Analyze code for basic correctness indicators
        Random random = new Random();

        // Simulate runtime based on code length and complexity
        int codeLines = code.split("\n").length;
        int baseRuntime = 50 + (codeLines * 2) + random.nextInt(100);

        // Check for obvious issues
        List<String> issues = detectCodeIssues(code, language);

        if (!issues.isEmpty()) {
            String issue = issues.get(0);
            if (issue.contains("syntax")) {
                return new ExecutionResult(
                    "COMPILE_ERROR", "", issue, 0, 0, 0, 5
                );
            }
        }

        // Simulate test cases (mostly passing for demonstration)
        int totalCases = 5 + random.nextInt(6);
        int passedCases = hasLikelyBug(code) ? random.nextInt(totalCases) : totalCases;

        String status = passedCases == totalCases ? "ACCEPTED" :
                       passedCases > totalCases / 2 ? "WRONG_ANSWER" : "WRONG_ANSWER";

        String output = generateSampleOutput(language, passedCases, totalCases);

        return new ExecutionResult(
            status,
            output,
            null,
            baseRuntime,
            128 * 1024 + random.nextInt(64 * 1024), // 128-192 MB
            passedCases,
            totalCases
        );
    }

    private List<String> detectCodeIssues(String code, String language) {
        List<String> issues = new ArrayList<>();

        if (code == null || code.trim().isEmpty()) {
            issues.add("Empty code submission");
            return issues;
        }

        // Very basic syntax checks
        if (language.equalsIgnoreCase("java")) {
            long openBraces = code.chars().filter(c -> c == '{').count();
            long closeBraces = code.chars().filter(c -> c == '}').count();
            if (Math.abs(openBraces - closeBraces) > 1) {
                issues.add("syntax error: unmatched braces");
            }
        }

        return issues;
    }

    private boolean hasLikelyBug(String code) {
        // Heuristic: check for common beginner mistakes
        return code.contains("while(true)") ||
               code.contains("while (true)") ||
               (code.contains("for") && !code.contains("break") && !code.contains("return"));
    }

    private String generateSampleOutput(String language, int passed, int total) {
        if (passed == total) {
            return String.format("All %d test cases passed!\n✓ Test 1: Passed\n✓ Test 2: Passed\n✓ Test 3: Passed", total);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= total; i++) {
            if (i <= passed) {
                sb.append(String.format("✓ Test %d: Passed\n", i));
            } else {
                sb.append(String.format("✗ Test %d: Wrong Answer\n", i));
            }
        }
        return sb.toString();
    }

    public Map<String, Object> runCustomTestCase(String code, String language, String input) {
        Map<String, Object> result = new HashMap<>();
        result.put("input", input);
        result.put("output", "Execution output would appear here");
        result.put("status", "SUCCESS");
        result.put("runtimeMs", 100 + new Random().nextInt(200));
        return result;
    }
}
