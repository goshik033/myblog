package ru.kolidgio.myblog.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserDto(
        @NotBlank @Size(max = 255) String username,
        @Email @NotBlank @Size(max = 255) String email
) {
}