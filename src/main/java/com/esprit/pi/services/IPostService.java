package com.esprit.pi.services;

import com.esprit.pi.dto.CommentRequest;
import com.esprit.pi.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPostService {
    List<Post> getAllPosts();
    Post getPostById(Long id);
    Page<Post> getPostsByHackathon(Long hackathonId, Pageable pageable);
    Post createPost(Post post, List<MultipartFile> images);
    Post updatePost(Long id, Post post, List<MultipartFile> images);
    void deletePost(Long id);
    Post likePost(Long postId, Long userId);
    Post addComment(Long postId, CommentRequest commentRequest);
    Post deleteComment(Long postId, Long commentId);
}