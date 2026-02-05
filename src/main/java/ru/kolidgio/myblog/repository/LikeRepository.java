package ru.kolidgio.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kolidgio.myblog.model.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByPost_Id(Long post_id);

    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    void deleteByUser_IdAndPost_Id(Long userId, Long postId);

}
