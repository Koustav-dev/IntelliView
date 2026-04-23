package com.intelliview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDTO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String rarity;
    private int points;
    private String earnedAt;
}
