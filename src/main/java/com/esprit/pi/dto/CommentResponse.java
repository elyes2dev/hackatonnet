package com.esprit.pi.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private String userFullName;
    private String userPicture;
    private Date createdAt;
}
