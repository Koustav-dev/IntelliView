package com.intelliview.controller;

import com.intelliview.service.SubmissionService;
import com.intelliview.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SubmissionService submissionService;

    @MessageMapping("/code-run")
    public void handleCodeRun(@Payload Map<String, Object> payload, Authentication auth) {
        String username = auth.getName();
        try {
            User user = (User) auth.getPrincipal();

            // Send acknowledgment
            messagingTemplate.convertAndSendToUser(
                username, "/queue/code-status",
                Map.of("status", "RUNNING", "message", "Executing your code...")
            );

            // Run code - result will be sent back via user queue
            messagingTemplate.convertAndSendToUser(
                username, "/queue/code-status",
                Map.of("status", "PROCESSING", "message", "Running test cases...")
            );

        } catch (Exception e) {
            log.error("WebSocket code run error: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                username, "/queue/code-status",
                Map.of("status", "ERROR", "message", "Execution failed: " + e.getMessage())
            );
        }
    }

    @MessageMapping("/interview-ping")
    public void handleInterviewPing(@Payload Map<String, Object> payload, Authentication auth) {
        String username = auth.getName();
        messagingTemplate.convertAndSendToUser(
            username, "/queue/interview-status",
            Map.of("type", "PONG", "timestamp", System.currentTimeMillis())
        );
    }
}
