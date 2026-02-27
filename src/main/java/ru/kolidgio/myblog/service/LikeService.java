package ru.kolidgio.myblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolidgio.myblog.model.Like;
import ru.kolidgio.myblog.repository.LikeRepository;
import ru.kolidgio.myblog.repository.PostRepository;
import ru.kolidgio.myblog.repository.UserRepository;
import ru.kolidgio.myblog.service.errors.BadRequestException;
import ru.kolidgio.myblog.service.errors.ConflictException;
import ru.kolidgio.myblog.service.errors.NotFoundException;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long toggle(Long postId, Long userId) {
        requireId(postId, "postId");
        requireId(userId, "userId");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User с id " + userId + " не найден");
        }
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Post с id " + postId + " не найден");
        }

        try {
            if (!likeRepository.existsByPost_IdAndUser_Id(postId, userId)) {
                Like like = new Like();
                like.setPost(postRepository.getReferenceById(postId));
                like.setUser(userRepository.getReferenceById(userId));
                likeRepository.save(like);
            } else {
                likeRepository.deleteByPost_IdAndUser_Id(postId, userId);
            }
            return likeRepository.countByPost_Id(postId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Не удалось изменить лайк из-за ограничения БД", e);
        }


    }

    @Transactional(readOnly = true)
    public long count(Long postId) {
        requireId(postId, "postId");
        return likeRepository.countByPost_Id(postId);
    }

    private static void requireId(Long id, String field) {
        if (id == null) throw new BadRequestException(field + " не должен быть null");
        if (id <= 0) throw new BadRequestException(field + " должен быть > 0");
    }

}
