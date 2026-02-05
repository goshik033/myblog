package ru.kolidgio.myblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePostDto(
        @NotBlank @Size(max=255) String title,
        @Size(max=512) String ImagePath,
        @NotBlank String content
) {
}
