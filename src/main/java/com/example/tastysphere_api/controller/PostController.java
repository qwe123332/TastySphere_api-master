package com.example.tastysphere_api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.service.PostService;
import com.example.tastysphere_api.service.RecommendationService;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails user) {
        // 获取 MyBatis-Plus 的 IPage 类型结果
        IPage<PostDTO> iPage = postService.getPosts(user.getUser(), PageRequest.of(page, size));
        // 将 IPage 转换为 Spring Data 的 Page 类型
        Page<PostDTO> postPage = new PageImpl<>(
                iPage.getRecords(),          // 内容列表
                PageRequest.of(page, size),  // 分页信息
                iPage.getTotal()             // 总记录数
        );
        return ResponseEntity.ok(postPage);
    }
    @PutMapping("/{postId}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> auditPost(@PathVariable Long postId, @RequestParam boolean approved) {
        postService.auditPost(postId, approved);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostDTO>> getPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 从 service 拿到分页后的自定义 IPage<PostDTO>，再构造 JPA 的 Page
        IPage<PostDTO> iPage = postService.getPostsByUser(userId, PageRequest.of(page, size));
        Page<PostDTO> postPage = new PageImpl<>(
                iPage.getRecords(),
                PageRequest.of(page, size),
                iPage.getTotal()
        );

        // 直接返回 Page 对象即可
        return ResponseEntity.ok(postPage);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        PostDTO postDTO = postService.getPost(postId);
        if (user != null) {
            recommendationService.recordUserView(user.getUserId(), postDTO); // ✅ 用 DTO 代替 Entity 传入
        }
        return ResponseEntity.ok(postDTO);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @Valid @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal CustomUserDetails user) throws BadRequestException {
        if (StringUtils.isBlank(postDTO.getTitle())) {
            throw new BadRequestException("标题不能为空");
        }
        return ResponseEntity.ok(postService.createPost(postDTO, user.getUser()));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostDTO postDTO,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(postService.updatePost(postId, postDTO, user.getUser()));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        postService.deletePost(postId, user.getUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostDTO>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        IPage<PostDTO> result = postService.searchPosts(keyword, pageable);

        Page<PostDTO> pageResult = new PageImpl<>(
                result.getRecords(), // 内容
                pageable,            // 分页信息
                result.getTotal()    // 总条数
        );

        return ResponseEntity.ok(pageResult);
    }


    @GetMapping("/recommended")
    public ResponseEntity<List<PostDTO>> getRecommendedPosts(
            @AuthenticationPrincipal CustomUserDetails user) {
        List<PostDTO> recommendedPosts = postService.getRecommendedPosts(
                user != null ? user.getUser() : null
        );
        return ResponseEntity.ok(recommendedPosts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        postService.likePost(postId, user.getUser());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        postService.unlikePost(postId, user.getUser());
        return ResponseEntity.ok().build();
    }
}
