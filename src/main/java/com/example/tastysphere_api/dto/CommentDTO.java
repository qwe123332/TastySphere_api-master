package com.example.tastysphere_api.dto;

import lombok.Data;

import java.util.List;

@Data

public class CommentDTO {
    private Long id;
    private String username;
    private String userAvatar;
    private String content;
    private Long userId;
    private Integer likeCount;
    private Long postId;
    private Long parentCommentId;
    private List<CommentDTO> replies;
    private String createdAt;
    private String updatedAt;
}
