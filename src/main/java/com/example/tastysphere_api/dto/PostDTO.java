package com.example.tastysphere_api.dto;

import com.example.tastysphere_api.enums.VisibilityEnum;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostDTO {
    private Long id;

    @NotNull
    private Long userId;

    private String title;
    private String username;
    private String userAvatar;

    @NotBlank(message = "Content cannot be empty")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    private String content;

    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<String> images = new ArrayList<>();

    private List<CommentDTO> commentDTOs = new ArrayList<>();

    @NotNull(message = "必须指定可见性")
    private VisibilityEnum visibility;

    private Integer likeCount = 0;
    private Integer commentCount = 0;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    private Boolean isLiked = false;
    private Boolean isMine = false;
    private Boolean audited = false;
    private Boolean approved = false;

    private String category; // 🔹 建议补充
    private List<TagDTO> tags;
    private List<Long> tagIds = new ArrayList<>();
    @Data
    public static class TagDTO {
        private Long id;
        private String name;
    }
}
