package ru.kolidgio.myblog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kolidgio.myblog.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
