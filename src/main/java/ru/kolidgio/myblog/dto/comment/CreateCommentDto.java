package ru.kolidgio.myblog.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CreateCommentDto(
        @NotNull Long userId,
        @NotBlank @Size(max = 2000) String content
) {
}
