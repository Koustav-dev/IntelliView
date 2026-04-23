package com.intelliview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SubmitCodeRequest {
    @NotNull
    private UUID problemId;

    @NotBlank
    private String language;

    @NotBlank
    private String code;

    private UUID sessionId;
}
