package com.esprit.pi.services;

import com.esprit.pi.dto.CommentRequest;
import com.esprit.pi.dto.CommentResponse;
import com.esprit.pi.entities.Comment;
import com.esprit.pi.entities.Post;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.ICommentRepository;
import com.esprit.pi.repositories.IPostRepository;
import com.esprit.pi.repositories.IUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CommentService implements ICommentService {

    private final ICommentRepository commentRepository;
    private final IPostRepository postRepository;
    private final IUserRepository userRepository;

    @Override
    public CommentResponse createComment(Long postId, CommentRequest commentRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + commentRequest.getUserId()));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPostedBy(user);
        comment.setPost(post);
        comment.setCreatedAt(new Date());

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentResponse(savedComment);
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        comment.setContent(commentRequest.getContent());
        comment.setUpdatedAt(new Date());

        Comment updatedComment = commentRepository.save(comment);
        return mapToCommentResponse(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        return mapToCommentResponse(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUserId(comment.getPostedBy().getId());
        response.setUserFullName(comment.getPostedBy().getName());
        response.setUserPicture(comment.getPostedBy().getPicture());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}