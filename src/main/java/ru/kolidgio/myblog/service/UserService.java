package ru.kolidgio.myblog.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kolidgio.myblog.model.User;
import ru.kolidgio.myblog.repository.UserRepository;
import ru.kolidgio.myblog.service.errors.NotFoundException;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getByEmailOrThrow(@Email @NotBlank String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("User с email " + normalizedEmail + " не найден"));

    }

    @Transactional(readOnly = true)
    public User getByUsernameOrThrow(@NotBlank String username) {
        String normalizedUsername = username.trim();
        return userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new NotFoundException("User с username " + normalizedUsername + " не найден"));

    }
    @Transactional
    public User create(User user) {

    }
}
