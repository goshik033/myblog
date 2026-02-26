package ru.kolidgio.myblog.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kolidgio.myblog.dto.comment.CreateCommentDto;
import ru.kolidgio.myblog.dto.comment.UpdateCommentDto;
import ru.kolidgio.myblog.model.Comment;
import ru.kolidgio.myblog.model.Post;
import ru.kolidgio.myblog.model.User;
import ru.kolidgio.myblog.repository.CommentRepository;
import ru.kolidgio.myblog.repository.PostRepository;
import ru.kolidgio.myblog.repository.UserRepository;
import ru.kolidgio.myblog.service.errors.BadRequestException;
import ru.kolidgio.myblog.service.errors.ConflictException;
import ru.kolidgio.myblog.service.errors.NotFoundException;

@Validated
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Comment getOrThrow(Long commentId) {
        requireId(commentId, "commentId");
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment с id " + commentId + " не найден"));
    }

    @Transactional
    public Comment create(Long postId, @Valid CreateCommentDto dto) {
        requireId(dto.userId(), "userId");
        requireId(postId, "postId");
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("User с id " + dto.userId() + " не найден"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post с id " + postId + " не найден"));
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(dto.content().trim());
        try {
            return commentRepository.save(comment);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось создать комментарий из-за ограничения БД", e);
        }
    }

    @Transactional
    public void delete(Long commentId) {
        requireId(commentId, "commentId");
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment с id " + commentId + " не найден");
        }
        try {
            commentRepository.deleteById(commentId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось удалить комментарий из-за ограничения БД", e);

        }
    }

    @Transactional
    public Comment update(Long commentId, @Valid UpdateCommentDto dto) {
        Comment comment = getOrThrow(commentId);
        comment.setContent(dto.content().trim());
        try {
            return commentRepository.save(comment);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось обновить комментарий из-за ограничения БД", e);

        }
    }

    @Transactional(readOnly = true)
    public Page<Comment> feed(Long postId, Pageable pageable) {
        requireId(postId, "postId");
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Post с id " + postId + " не найден");
        }
        if (pageable == null) {
            throw new BadRequestException("Pageable не должен быть null");
        }

        return commentRepository.findByPost_IdOrderByCreatedAtAsc(postId, pageable);


    }


    private static void requireId(Long id, String field) {
        if (id == null) throw new BadRequestException(field + " не должен быть null");
        if (id <= 0) throw new BadRequestException(field + " должен быть > 0");
    }

}
