package ru.kolidgio.myblog.dto.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCommentDto(
        @NotNull @Size(max = 2000) String content
) {
}
