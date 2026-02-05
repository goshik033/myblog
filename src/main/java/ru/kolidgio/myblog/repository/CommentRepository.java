package ru.kolidgio.myblog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kolidgio.myblog.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByUser_Id(Long userId, Pageable pageable);

    Page<Comment> findByPost_IdOrderByCreatedAtAsc(Long postId, Pageable pageable);

    long countByPost_id(Long postId);
}
