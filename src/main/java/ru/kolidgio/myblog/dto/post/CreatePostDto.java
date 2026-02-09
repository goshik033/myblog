package ru.kolidgio.myblog.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostDto(
        @NotNull Long userId,
        @NotBlank @Size(max = 255) String title,
        @Size(max = 512) String imagePath,
        @NotBlank String content
) {
}