package ru.kolidgio.myblog.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
        @NotBlank @Size(min=6,max=255) String oldPassword,
        @NotBlank @Size(min=6,max=255) String newPassword
) {
}
