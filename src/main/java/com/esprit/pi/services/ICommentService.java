package com.esprit.pi.services;

import com.esprit.pi.dto.CommentRequest;
import com.esprit.pi.dto.CommentResponse;
import java.util.List;

public interface ICommentService {
    CommentResponse createComment(Long postId, CommentRequest commentRequest);
    CommentResponse updateComment(Long commentId, CommentRequest commentRequest);
    void deleteComment(Long commentId);
    CommentResponse getCommentById(Long commentId);
    List<CommentResponse> getCommentsByPostId(Long postId);
}