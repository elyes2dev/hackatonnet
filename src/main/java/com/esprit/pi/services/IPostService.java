package com.esprit.pi.services;

import com.esprit.pi.entities.Post;

import java.util.List;

public interface IPostService {
    List<Post> getAllPosts();
    Post getPostById(Long id);
    Post createPost(Post post);
    Post updatePost(Long id, Post post);
    void deletePost(Long id);
}
