package ru.kolidgio.myblog.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kolidgio.myblog.dto.CreatePostDto;
import ru.kolidgio.myblog.dto.UpdatePostDto;
import ru.kolidgio.myblog.model.Post;
import ru.kolidgio.myblog.model.User;
import ru.kolidgio.myblog.repository.PostRepository;
import ru.kolidgio.myblog.repository.UserRepository;
import ru.kolidgio.myblog.service.errors.BadRequestException;
import ru.kolidgio.myblog.service.errors.ConflictException;
import ru.kolidgio.myblog.service.errors.NotFoundException;

@Validated
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<Post> feedByUser(Long userId, Pageable pageable) {
        requireId(userId, "userId");
        if (pageable == null) {
            throw new BadRequestException("Pageable не должен быть null");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User с id " + userId + " не найден");
        }
        return postRepository.findAllByUser_IdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> feed(Pageable pageable) {
        if (pageable == null) {
            throw new BadRequestException("Pageable не должен быть null");
        }
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Post getOrThrow(Long postId) {
        requireId(postId, "postId");
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post с id " + postId + " не найден"));
    }

    @Transactional
    public Post create(@Valid CreatePostDto dto) {
        requireId(dto.userId(), "userId");
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("User с id " + dto.userId() + " не найден"));
        Post post = new Post();
        post.setUser(user);
        post.setTitle(dto.title().trim());
        post.setImagePath(normalizeNullable(dto.imagePath()));
        post.setContent(dto.content().trim());

        try {
            return postRepository.save(post);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось создать пост из-за ограничения БД", e);
        }

    }

    @Transactional
    public Post update(Long postId, @Valid UpdatePostDto dto) {
        requireId(postId, "postId");
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post с id " + postId + " не найден"));
        post.setTitle(dto.title().trim());
        post.setContent(dto.content().trim());
        post.setImagePath(normalizeNullable(dto.imagePath()));
        try {
            return postRepository.save(post);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось обновить пост из-за ограничения БД", e);
        }

    }

    @Transactional
    public void delete(Long postId) {
        requireId(postId, "postId");
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Post с id " + postId + " не найден");
        }
        try {
            postRepository.deleteById(postId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось удалить пост из-за ограничения БД", e);
        }
    }

    private static void requireId(Long id, String field) {
        if (id == null) throw new BadRequestException(field + " не должен быть null");
        if (id <= 0) throw new BadRequestException(field + " должен быть > 0");
    }

    private static String normalizeNullable(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
