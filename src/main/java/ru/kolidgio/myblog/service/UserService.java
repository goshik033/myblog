package ru.kolidgio.myblog.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kolidgio.myblog.dto.user.ChangePasswordDto;
import ru.kolidgio.myblog.dto.user.CreateUserDto;
import ru.kolidgio.myblog.dto.user.UpdateUserDto;
import ru.kolidgio.myblog.model.User;
import ru.kolidgio.myblog.repository.UserRepository;
import ru.kolidgio.myblog.service.errors.BadRequestException;
import ru.kolidgio.myblog.service.errors.ConflictException;
import ru.kolidgio.myblog.service.errors.NotFoundException;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getByEmail(@Email @NotBlank String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("User с email " + normalizedEmail + " не найден"));

    }

    @Transactional(readOnly = true)
    public User getByUsername(@NotBlank String username) {
        String normalizedUsername = username.trim();
        return userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new NotFoundException("User с username " + normalizedUsername + " не найден"));

    }

    @Transactional(readOnly = true)
    public User getById(Long userId) {
        requireId(userId, "userId");
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с userId " + userId + " не найден"));
    }

    @Transactional
    public User create(CreateUserDto dto) {
        String email = dto.email().trim().toLowerCase();
        String username = dto.username().trim();

        if (userRepository.existsByEmail(email))
            throw new ConflictException("Email уже занят " + email);

        if (userRepository.existsByUsername(username))
            throw new ConflictException("Username уже занят " + username);

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setUsername(username);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось сохранить пользователя из-за ограничений БД", e);
        }


    }

    @Transactional
    public User update(Long userId, UpdateUserDto dto) {
        requireId(userId, "userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с userId " + userId + " не найден"));

        String email = dto.email().trim().toLowerCase();
        String username = dto.username().trim();

        if (!user.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmail(email))
            throw new ConflictException("User с email " + email + " уже существует");

        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username))
            throw new ConflictException("User с username " + username + " уже существует");

        user.setEmail(email);
        user.setUsername(username);
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось изменить пользователя из-за ограничений БД", e);
        }

    }

    @Transactional
    public void delete(Long userId) {
        requireId(userId, "userId");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User с userId " + userId + " не найден");
        }
        try {
            userRepository.deleteById(userId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось удалить пользователя из-за ограничений БД", e);
        }

    }

    @Transactional
    public User changePassword(Long userId, ChangePasswordDto dto) {
        requireId(userId, "userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с userId " + userId + " не найден"));
        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword()))
            throw new BadRequestException("Неверный старый пароль");
        if (dto.newPassword() == null || dto.newPassword().trim().isEmpty()) {
            throw new BadRequestException("Новый пароль не должен быть пустым");
        }
        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new BadRequestException("Новый пароль должен отличаться от старого");
        }
        user.setPassword(passwordEncoder.encode(dto.newPassword()));

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось изменить пароль пользователя из-за ограничений БД", e);

        }
    }


    private static void requireId(Long id, String field) {
        if (id == null) throw new BadRequestException(field + " не должен быть null");
        if (id <= 0) throw new BadRequestException(field + " должен быть > 0");
    }

}
