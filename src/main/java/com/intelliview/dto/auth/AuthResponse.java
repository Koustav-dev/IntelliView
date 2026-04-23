package com.intelliview.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String email;
        private String username;
        private String fullName;
        private String avatarUrl;
        private String role;
        private String experienceLevel;
        private int streakCount;
        private int totalInterviews;
        private int totalProblemsSolved;
        private boolean isVerified;
    }
}
