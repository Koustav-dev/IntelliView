package com.intelliview.scheduler;

import com.intelliview.repository.RefreshTokenRepository;
import com.intelliview.repository.UserMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final RefreshTokenRepository refreshTokenRepo;

    // Clean up expired refresh tokens every 6 hours
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    @Transactional
    public void cleanExpiredTokens() {
        refreshTokenRepo.deleteExpiredTokens(LocalDateTime.now());
        log.debug("🧹 Cleaned expired refresh tokens.");
    }

    // Log health check every hour
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void healthCheck() {
        log.debug("💓 IntelliView backend is running.");
    }
}
