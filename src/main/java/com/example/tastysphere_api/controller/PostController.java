package com.example.tastysphere_api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.dto.PostDraftDTO;
import com.example.tastysphere_api.dto.ReportRequestDTO;
import com.example.tastysphere_api.service.*;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired private PostService postService;
    @Autowired private RecommendationService recommendationService;
    @Autowired private PostReportService postReportService;
    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private PostDraftService postDraftService;

    @GetMapping
    public ResponseEntity<IPage<PostDTO>> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(postService.getPosts(user.getUser(), page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<IPage<PostDTO>> getPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, page, size));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        PostDTO postDTO = postService.getPost(postId);
        if (user != null) {
            recommendationService.recordUserView(user.getUserId(), postDTO);
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

    @GetMapping("/{postId}/stats")
    public ResponseEntity<Map<String, Integer>> getPostStats(@PathVariable Long postId) {
        Map<String, Integer> stats = postService.getPostStats(postId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/report")
    public ResponseEntity<String> reportContent(
            @RequestBody ReportRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails user) {
        postReportService.reportContent(
                request.getPostId(), request.getCommentId(),
                user.getUser().getId(), request.getReason()
        );
        return ResponseEntity.ok("举报成功，感谢你的反馈！");
    }

    @PostMapping("/drafts")
    public ResponseEntity<PostDraftDTO> saveDraft(
            @RequestBody PostDraftDTO draft,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(postDraftService.saveDraft(draft, user.getUser().getId()));
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<PostDraftDTO>> getMyDrafts(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(postDraftService.getMyDrafts(user.getUser().getId()));
    }

    @DeleteMapping("/drafts/{draftId}")
    public ResponseEntity<Void> deleteDraft(
            @PathVariable Long draftId,
            @AuthenticationPrincipal CustomUserDetails user) {
        postDraftService.deleteDraft(draftId, user.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> auditPost(@PathVariable Long postId, @RequestParam boolean approved) {
        postService.auditPost(postId, approved);
        return ResponseEntity.ok().build();
    }
}
