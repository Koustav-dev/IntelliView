package com.intelliview.controller;

import com.intelliview.dto.ApiResponse;
import com.intelliview.dto.auth.*;
import com.intelliview.model.User;
import com.intelliview.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token is required"));
        }
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal User user) {
        authService.logout(user);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@PathVariable String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody Map<String, String> body) {
        authService.forgotPassword(body.get("email"));
        return ResponseEntity.ok(ApiResponse.success("If the email exists, a reset link has been sent", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody Map<String, String> body) {
        authService.resetPassword(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getMe(@AuthenticationPrincipal User user) {
        AuthResponse.UserInfo info = AuthResponse.UserInfo.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .experienceLevel(user.getExperienceLevel().name())
                .streakCount(user.getStreakCount())
                .totalInterviews(user.getTotalInterviews())
                .totalProblemsSolved(user.getTotalProblemsSolved())
                .isVerified(user.getIsVerified())
                .build();
        return ResponseEntity.ok(ApiResponse.success(info));
    }
}
