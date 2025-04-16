package com.esprit.pi.controllers;

import com.esprit.pi.dto.CommentRequest;
import com.esprit.pi.dto.CommentResponse;
import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.entities.Post;
import com.esprit.pi.entities.User;
import com.esprit.pi.services.IPostService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/pi/posts")
public class PostController {
    @Autowired
    private final IPostService postService;

    CommentController commentController;



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestPart("post") String postStr,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode postNode = objectMapper.readTree(postStr);

            // Create post from JSON
            Post post = new Post();
            post.setTitle(postNode.get("title").asText());
            post.setContent(postNode.get("content").asText());

            // Set relationships by ID
            User user = new User();
            user.setId(postNode.get("postedBy").asLong());
            post.setPostedBy(user);

            Hackathon hackathon = new Hackathon();
            hackathon.setId(postNode.get("hackathon").asLong());
            post.setHackathon(hackathon);

            Post savedPost = postService.createPost(post, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating post: " + e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestPart Post post,
            @RequestPart(required = false) List<MultipartFile> images) {
        try {
            Post updatedPost = postService.updatePost(id, post, images);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Post deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<Map<String, Object>> getPostsByHackathon(
            @PathVariable Long hackathonId,
            Pageable pageable) {

        Page<Post> page = postService.getPostsByHackathon(hackathonId, pageable);

        // Force initialization of all relationships
        page.getContent().forEach(post -> {
            Hibernate.initialize(post.getPostedBy());
            Hibernate.initialize(post.getHackathon());
            Hibernate.initialize(post.getLikes());
            Hibernate.initialize(post.getComments());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("size", page.getSize());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/like/{userId}")
    public ResponseEntity<?> likePost(
            @PathVariable Long postId,
            @PathVariable Long userId) {
        try {
            Post post = postService.likePost(postId, userId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest commentRequest) {
        try {
            Post post = postService.addComment(postId, commentRequest);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        try {
            Post post = postService.deleteComment(postId, commentId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addCommentToPost(
            @PathVariable Long postId,
            @RequestBody CommentRequest commentRequest) {

        return commentController.createComment(postId, commentRequest);
    }
}

