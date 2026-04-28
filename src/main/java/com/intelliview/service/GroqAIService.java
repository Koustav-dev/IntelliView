package com.intelliview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class GroqAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.base-url}")
    private String baseUrl;

    @Value("${groq.api.model}")
    private String model;

    @Value("${groq.api.max-tokens}")
    private int maxTokens;

    @Value("${groq.api.temperature}")
    private double temperature;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GroqAIService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    public String chat(String systemPrompt, String userMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);

            List<Map<String, String>> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                messages.add(Map.of("role", "system", "content", systemPrompt));
            }
            messages.add(Map.of("role", "user", "content", userMessage));
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/chat/completions", entity, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Groq API error: {}", e.getMessage());
            return "AI analysis temporarily unavailable. Please try again later.";
        }
    }

    public Map<String, Object> analyzeCode(String code, String language, String problemTitle,
                                            String problemDescription) {
        String systemPrompt = """
            You are an expert code reviewer and algorithm specialist. Analyze the submitted code 
            and provide structured feedback. Return your analysis as a valid JSON object with these exact keys:
            {
                "timeComplexity": "O(n) explanation",
                "spaceComplexity": "O(n) explanation",
                "codeQualityScore": 85,
                "allTestCasesPassed": true,
                "correctness": "Analysis of correctness",
                "efficiency": "Analysis of efficiency",
                "readability": "Analysis of readability",
                "bestPractices": "List of best practices used/violated",
                "improvements": ["specific improvement 1", "specific improvement 2"],
                "strengths": ["strength 1", "strength 2"],
                "overallFeedback": "Comprehensive overall feedback",
                "optimizedApproach": "Brief description of optimal approach"
            }
            
            IMPORTANT:
            - Set "allTestCasesPassed" to true ONLY IF the code has PERFECT correctness, handles ALL edge cases (nulls, empty arrays, limits), has no logical or syntax errors, and exactly matches the requested time/space complexity if specified. If there is ANY bug or flaw, set it to false.
            - Provide your response ONLY as valid JSON. Do not add markdown backticks outside or any conversational text.
            """;

        String userMessage = String.format(
            "Problem: %s\n\nDescription: %s\n\nLanguage: %s\n\nCode:\n```%s\n%s\n```",
            problemTitle, problemDescription, language, language, code
        );

        String response = chat(systemPrompt, userMessage);

        try {
            // Extract JSON from response
            int start = response.indexOf('{');
            int end = response.lastIndexOf('}') + 1;
            if (start >= 0 && end > start) {
                String jsonStr = response.substring(start, end);
                return objectMapper.readValue(jsonStr, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            }
        } catch (Exception e) {
            log.warn("Failed to parse AI code review JSON: {}", e.getMessage());
        }

        return Map.of("overallFeedback", response, "codeQualityScore", 70);
    }

    public Map<String, Object> analyzeBehavioralResponse(String question, String response, List<String> keywords) {
        String systemPrompt = """
            You are an expert behavioral interview coach. Analyze the candidate's response using the STAR method
            (Situation, Task, Action, Result). Return a JSON object with:
            {
                "starScore": 75,
                "clarityScore": 80,
                "relevanceScore": 85,
                "overallScore": 80,
                "starAnalysis": {
                    "situation": "was situation clearly described?",
                    "task": "was the task defined?",
                    "action": "were actions specific?",
                    "result": "was the result quantified?"
                },
                "strengths": ["strength1", "strength2"],
                "improvements": ["improvement1", "improvement2"],
                "keywordsUsed": ["keyword1"],
                "detailedFeedback": "Comprehensive feedback",
                "improvedResponse": "Brief example of improved response structure"
            }
            """;

        String userMessage = String.format(
            "Interview Question: %s\n\nCandidate's Response: %s\n\nExpected Keywords: %s",
            question, response, String.join(", ", keywords != null ? keywords : List.of())
        );

        String aiResponse = chat(systemPrompt, userMessage);

        try {
            int start = aiResponse.indexOf('{');
            int end = aiResponse.lastIndexOf('}') + 1;
            if (start >= 0 && end > start) {
                String jsonStr = aiResponse.substring(start, end);
                return objectMapper.readValue(jsonStr, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            }
        } catch (Exception e) {
            log.warn("Failed to parse behavioral analysis JSON: {}", e.getMessage());
        }

        return Map.of("detailedFeedback", aiResponse, "overallScore", 70);
    }

    public String generateInterviewFeedback(String sessionType, int score, List<String> strengths,
                                             List<String> weaknesses, String company) {
        String prompt = String.format("""
            Generate a detailed, personalized interview feedback report for a candidate who completed 
            a %s interview%s with a score of %d/100.
            
            Strengths demonstrated: %s
            Areas for improvement: %s
            
            Provide:
            1. Executive summary (2-3 sentences)
            2. Technical assessment
            3. Key strengths to maintain
            4. Priority improvement areas with specific action items
            5. Study recommendations with resources
            6. Next steps and timeline (2-4 weeks plan)
            
            Be specific, encouraging, and actionable.
            """,
            sessionType,
            company != null ? " targeting " + company : "",
            score,
            String.join(", ", strengths != null ? strengths : List.of()),
            String.join(", ", weaknesses != null ? weaknesses : List.of())
        );

        return chat("You are a senior technical interviewer providing mentorship feedback.", prompt);
    }

    public String generateHint(String problemTitle, String problemDescription, String userCode,
                                String language, int hintLevel) {
        String prompt = String.format("""
            Problem: %s
            Description: %s
            User's current code (%s):
            ```
            %s
            ```
            Hint level requested: %d/3 (1=subtle hint, 2=moderate hint, 3=direct approach hint)
            
            Provide a helpful hint at the requested level without giving away the complete solution.
            """, problemTitle, problemDescription, language, userCode, hintLevel);

        return chat("You are a Socratic coding mentor. Guide without revealing the answer directly.", prompt);
    }

    public String generateProblem(String difficulty, String topic, String company) {
        String prompt = String.format("""
            Generate a coding interview problem with the following criteria:
            - Difficulty: %s
            - Topic/Category: %s
            - Company style: %s
            
            Return a JSON object with:
            {
                "title": "Problem Title",
                "description": "Full problem description with constraints",
                "examples": [{"input": "...", "output": "...", "explanation": "..."}],
                "constraints": "1 <= n <= 10^5",
                "hints": ["hint1", "hint2"],
                "timeComplexity": "O(n log n)",
                "spaceComplexity": "O(n)"
            }
            """, difficulty, topic, company != null ? company : "General");

        String response = chat("You are an expert technical interviewer creating LeetCode-style problems.", prompt);

        return response;
    }

    public String explainSolution(String problemTitle, String code, String language) {
        String prompt = String.format("""
            Explain this solution for the problem "%s" written in %s:
            ```%s
            %s
            ```
            
            Provide:
            1. Algorithm approach (in simple terms)
            2. Step-by-step walkthrough with an example
            3. Why this approach works
            4. Time and space complexity explanation
            5. Alternative approaches considered
            """, problemTitle, language, language, code);

        return chat("You are an expert algorithm teacher who explains complex concepts simply.", prompt);
    }

    public Map<String, Object> generateStudyPlan(String targetCompany, String experienceLevel,
                                                   int weeksAvailable, List<String> weakAreas) {
        String prompt = String.format("""
            Create a personalized %d-week study plan for a %s level developer targeting %s.
            Known weak areas: %s
            
            Return a JSON with:
            {
                "weeklyPlan": [
                    {
                        "week": 1,
                        "focus": "Arrays & Strings",
                        "topics": ["Two pointers", "Sliding window"],
                        "problems": ["Two Sum", "Valid Palindrome"],
                        "resources": ["LeetCode Easy arrays", "YouTube tutorial link"],
                        "timeCommitment": "10 hours"
                    }
                ],
                "overallStrategy": "...",
                "priorityTopics": ["topic1", "topic2"],
                "mockInterviewSchedule": "..."
            }
            """,
            weeksAvailable, experienceLevel,
            targetCompany != null ? targetCompany : "top tech companies",
            String.join(", ", weakAreas != null ? weakAreas : List.of())
        );

        String response = chat("You are an expert coding interview coach.", prompt);

        try {
            int start = response.indexOf('{');
            int end = response.lastIndexOf('}') + 1;
            if (start >= 0 && end > start) {
                return objectMapper.readValue(response.substring(start, end),
                        new com.fasterxml.jackson.core.type.TypeReference<>() {});
            }
        } catch (Exception e) {
            log.warn("Failed to parse study plan JSON");
        }

        return Map.of("overallStrategy", response);
    }
}
