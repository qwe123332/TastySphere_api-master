package com.example.tastysphere_api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.dto.mapper.CommentDtoMapper;
import com.example.tastysphere_api.entity.Comment;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.service.SensitiveWordService;
import com.example.tastysphere_api.service.SocialService;
import com.example.tastysphere_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/social")
public class SocialController {
    @Autowired
    private SocialService socialService;

    @Autowired
    private UserService userService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @PostMapping("/posts/{postId}/comments")
    public Comment createComment(
            @PathVariable Long postId,
            @RequestParam(required = false) Long parentId,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails user) {
        // 频率限制检查
        String key = "comment_limit:" + user.getUser().getId();
        String count = redisTemplate.opsForValue().get(key);
        if (Integer.parseInt(count) >= 10) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "评论次数超过限制，请稍后再试");
        }

        // 更新计数器
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);

        // 敏感词过滤
        String filteredContent = sensitiveWordService.filterContent(content);
        
        return socialService.createComment(postId, parentId, filteredContent, user.getUser());
    }

    @PostMapping("/posts/{postId}/like")
    public void toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        socialService.toggleLike(postId, user.getUser());
    }
    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<Void> toggleFollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user) {
        // 获取目标用户（Service 层返回 Optional<User>）
        UserDTO following = userService.getUserById(userId);

        // 执行关注/取关操作
        socialService.toggleFollow(user.getUser(), following);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/comments")
    public IPage<CommentDTO> getPostComments(
            @PathVariable Long postId,
            Pageable pageable) {
        return socialService.getPostComments(postId, pageable);

    }

    @GetMapping("/comments/{commentId}/replies")
    public IPage<CommentDTO> getCommentReplies(
            @PathVariable Long commentId,
            Pageable pageable) {
        return socialService.getCommentReplies(commentId, pageable);

    }
}