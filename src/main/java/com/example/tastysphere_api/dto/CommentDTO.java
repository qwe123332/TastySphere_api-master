package com.example.tastysphere_api.dto;

import com.example.tastysphere_api.entity.Comment;
import com.example.tastysphere_api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentDTO(Comment comment, User user) {
        this.id = comment.getId();
        this.username = user.getUsername();
        this.userAvatar = user.getAvatar();
        this.content = comment.getContent();
        this.userId = comment.getUserId();
        this.likeCount = comment.getLikeCount();
        this.postId = comment.getPostId();
        this.parentCommentId = comment.getParentCommentId();
        this.createdAt = comment.getCreatedTime();
        this.updatedAt = comment.getUpdatedTime();
    }

    public CommentDTO(Long id, String content, LocalDateTime createdTime, String username, String avatar) {
        this.id = id;
        this.content = content;
        this.createdAt = createdTime;
        this.username = username;
        this.userAvatar = avatar;
    }
}
