package com.example.tastysphere_api.controller.admin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.service.PostService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/posts")
@PreAuthorize("hasRole('ADMIN')") // 整个类只允许管理员访问
public class AdminPostController {

    @Autowired
    private PostService postService;

    /**
     * 获取待审核的帖子分页列表
     */
    @GetMapping("/pending")
    public ResponseEntity<IPage<PostDTO>> getPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        IPage<PostDTO> pendingPosts = postService.getPendingPosts(new Page<>(page + 1, size)); // ✅ MP 页码从 1 开始
        return ResponseEntity.ok(pendingPosts);
    }


    /**
     * 审核帖子（通过 / 拒绝）
     */
    @PostMapping("/{postId}/audit")
    public ResponseEntity<Void> auditPost(
            @PathVariable Long postId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal CustomUserDetails admin) {
        postService.auditPost(postId, approved, admin.getUser(), reason);
        return ResponseEntity.ok().build();
    }

    /**
     * 管理员删除任意帖子
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePostByAdmin(@PathVariable Long postId) {
        postService.adminDelete(postId);
        return ResponseEntity.ok().build();
    }
    /**
     * 获取所有帖子（分页）
     */
    @GetMapping
    public ResponseEntity<IPage<PostDTO>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        IPage<PostDTO> posts = postService.getAllPosts(new Page<>(page + 1, size), search);
        return ResponseEntity.ok(posts);
    }
    /**
     * 获取单个帖子详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        PostDTO post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }




}
