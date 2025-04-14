package com.esprit.pi.services;

import com.esprit.pi.dto.CommentRequest;
import com.esprit.pi.entities.*;
import com.esprit.pi.repositories.IHackathonRepository;
import com.esprit.pi.repositories.IPostRepository;
import com.esprit.pi.repositories.IUserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService implements IPostService {

    private static final String UPLOAD_DIR = "./uploads/posts/";

    private final IPostRepository postRepository;
    private final IHackathonRepository hackathonRepository;
    private final IUserRepository userRepository;

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

//    @Override
//    public Page<Post> getPostsByHackathon(Long hackathonId, Pageable pageable) {
//        return postRepository.findByHackathonIdOrderByCreatedAtDesc(hackathonId, pageable);
//    }

    @Override
    public Page<Post> getPostsByHackathon(Long hackathonId, Pageable pageable) {
        Page<Post> posts = postRepository.findByHackathonIdOrderByCreatedAtDesc(hackathonId, pageable);
        // Force loading of relationships
        posts.getContent().forEach(post -> {
            Hibernate.initialize(post.getPostedBy());
            Hibernate.initialize(post.getHackathon());
            // Also initialize likes and comments if needed
            Hibernate.initialize(post.getLikes());
            Hibernate.initialize(post.getComments());
        });
        return posts;
    }

    @Override
    @Transactional
    public Post createPost(Post post, List<MultipartFile> images) {
        validatePostRequirements(post);

        Hackathon hackathon = hackathonRepository.findById(post.getHackathon().getId())
                .orElseThrow(() -> new RuntimeException("Hackathon not found with id: " + post.getHackathon().getId()));

        User postedBy = userRepository.findById(post.getPostedBy().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + post.getPostedBy().getId()));

        post.setHackathon(hackathon);
        post.setPostedBy(postedBy);
        post.setCreatedAt(new Date());
        post.setLikes(new HashSet<>());
        post.setComments(new ArrayList<>());

        if (images != null && !images.isEmpty()) {
            List<String> imagePaths = saveUploadedFiles(images);
            post.setImages(imagePaths);
        }

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Long id, Post postDetails, List<MultipartFile> images) {
        Post existingPost = getPostById(id);

        existingPost.setTitle(postDetails.getTitle());
        existingPost.setContent(postDetails.getContent());
        existingPost.setUpdatedAt(new Date());

        if (images != null && !images.isEmpty()) {
            // Delete old images if needed
            if (existingPost.getImages() != null) {
                existingPost.getImages().forEach(this::deleteImage);
            }
            List<String> newImagePaths = saveUploadedFiles(images);
            existingPost.setImages(newImagePaths);
        }

        return postRepository.save(existingPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = getPostById(id);

        // Delete associated images
        if (post.getImages() != null) {
            post.getImages().forEach(this::deleteImage);
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public Post likePost(Long postId, Long userId) {
        Post post = getPostById(postId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (post.getLikes().contains(user)) {
            post.getLikes().remove(user); // Unlike
        } else {
            post.getLikes().add(user); // Like
        }

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post addComment(Long postId, CommentRequest commentRequest) {
        Post post = getPostById(postId);
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + commentRequest.getUserId()));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPostedBy(user);
        comment.setCreatedAt(new Date());

        post.getComments().add(comment);
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post deleteComment(Long postId, Long commentId) {
        Post post = getPostById(postId);

        Comment commentToRemove = post.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        post.getComments().remove(commentToRemove);
        return postRepository.save(post);
    }

    private void validatePostRequirements(Post post) {
        if (post.getHackathon() == null || post.getHackathon().getId() == null) {
            throw new RuntimeException("Hackathon is required for a post.");
        }
        if (post.getPostedBy() == null || post.getPostedBy().getId() == null) {
            throw new RuntimeException("User is required for a post.");
        }
    }

    private List<String> saveUploadedFiles(List<MultipartFile> files) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            return files.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        try {
                            String fileName = UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename());
                            Path filePath = uploadPath.resolve(fileName);
                            Files.copy(file.getInputStream(), filePath);
                            return UPLOAD_DIR + fileName;
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to store file", e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory", e);
        }
    }

    private void deleteImage(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image: " + imagePath, e);
        }
    }
}