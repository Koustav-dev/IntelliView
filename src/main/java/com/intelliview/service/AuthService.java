package com.intelliview.service;

import com.intelliview.dto.auth.*;
import com.intelliview.model.RefreshToken;
import com.intelliview.model.User;
import com.intelliview.repository.RefreshTokenRepository;
import com.intelliview.repository.UserRepository;
import com.intelliview.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        User.ExperienceLevel level = User.ExperienceLevel.BEGINNER;
        if (request.getExperienceLevel() != null) {
            try { level = User.ExperienceLevel.valueOf(request.getExperienceLevel().toUpperCase()); }
            catch (Exception ignored) {}
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .experienceLevel(level)
                .verificationToken(UUID.randomUUID().toString())
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update last active
        user.setLastActiveDate(java.time.LocalDate.now());
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token expired. Please login again.");
        }

        User user = refreshToken.getUser();

        // Rotate refresh token
        refreshTokenRepository.delete(refreshToken);
        return buildAuthResponse(user);
    }

    public void logout(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));
        user.setIsVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setResetToken(UUID.randomUUID().toString());
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
            // Email sending would go here
            log.info("Password reset token generated for: {}", email);
        });
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtils.generateToken(user);
        String refreshTokenStr = UUID.randomUUID().toString();

        // Save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .expiresIn(86400)
                .user(AuthResponse.UserInfo.builder()
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
                        .build())
                .build();
    }
}
