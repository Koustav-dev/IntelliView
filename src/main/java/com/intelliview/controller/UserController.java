package com.intelliview.controller;

import com.intelliview.dto.ApiResponse;
import com.intelliview.dto.UserStatsDTO;
import com.intelliview.model.User;
import com.intelliview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<UserStatsDTO>> getMyStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserStats(user)));
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> updates) {
        userService.updateProfile(user, updates);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", null));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        userService.changePassword(user, body.get("oldPassword"), body.get("newPassword"), passwordEncoder);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLeaderboard(
            @RequestParam(defaultValue = "ALL_TIME") String period) {
        return ResponseEntity.ok(ApiResponse.success(userService.getLeaderboard(period)));
    }
}
