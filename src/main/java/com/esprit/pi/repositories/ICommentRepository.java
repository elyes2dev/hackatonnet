package com.esprit.pi.repositories;

import com.esprit.pi.entities.Comment;
import com.esprit.pi.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
}
