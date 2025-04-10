package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.dto.mapper.PostDtoMapper;
import com.example.tastysphere_api.enums.VisibilityEnum;
import com.example.tastysphere_api.mapper.PostMapper;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @Autowired
    private PostDtoMapper postDtoMapper;
    private final UserService userService;

    @Autowired
    public PostService(@Lazy UserService userService) {  // 添加 @Lazy 注解
        this.userService = userService;
    }
    public PostDTO getPost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        if (!post.getApproved() && post.getAudited()) {
            throw new ResourceNotFoundException("Post is not available");
        }

        return convertToDTO(post);
    }

    public IPage<PostDTO> getPosts(User user, Pageable pageable) {
        Page<Post> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        if (user != null && user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
            wrapper.eq(Post::getAudited, true);
        } else {
            wrapper.eq(Post::getAudited, true).eq(Post::getApproved, true);
        }

        Page<Post> result = postMapper.selectPage(mpPage, wrapper);
        return result.convert(this::convertToDTO);
    }

    public PostDTO createPost(PostDTO postDTO, User user) {
        Post post = new Post();
        post.setUserId(user.getId());
        post.setContent(sensitiveWordService.filterContent(postDTO.getContent()));
        post.setVisibility(postDTO.getVisibility());
        post.setImages(postDTO.getImages());
        post.setAudited(false);
        post.setApproved(false);

        postMapper.insert(post);
        return convertToDTO(post);
    }
// 注入用户服务

    private PostDTO convertToDTO(Post post) {
        User user = userService.getUserById(post.getUserId()); // 查询用户
        return postDtoMapper.toDTOWithUserAndContext(post, user, null, null);
    }


    public List<PostDTO> getUserPosts(Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId);
        List<Post> posts = postMapper.selectList(wrapper);
        return posts.stream().map(this::convertToDTO).toList();
    }

    public void deletePost(Long postId, User currentUser) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found");
        }

        if (!post.getUserId().equals(currentUser.getId()) &&
                currentUser.getRoles().stream().noneMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }

        postMapper.deleteById(postId);
    }

    public IPage<PostDTO> getPostsByUser(Long userId, Pageable pageable) {
        Page<Post> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId);

        Page<Post> result = postMapper.selectPage(mpPage, wrapper);
        return result.convert(this::convertToDTO);
    }

    public PostDTO updatePost(Long postId, PostDTO postDTO, User currentUser) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found");
        }

        if (!post.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this post");
        }

        post.setContent(sensitiveWordService.filterContent(postDTO.getContent()));
        post.setVisibility(postDTO.getVisibility());
        post.setImages(postDTO.getImages());
        post.setAudited(false);
        post.setApproved(false);

        postMapper.updateById(post);
        return convertToDTO(post);
    }

    public IPage<PostDTO> searchPosts(String keyword, Pageable pageable) {
        Page<Post> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Post::getContent, keyword);

        Page<Post> result = postMapper.selectPage(mpPage, wrapper);
        return result.convert(this::convertToDTO);
    }

    public List<PostDTO> getRecommendedPosts(User user) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getAudited, true).eq(Post::getApproved, true).orderByDesc(Post::getCreatedTime);
        Page<Post> page = new Page<>(0, 10);
        List<Post> posts = postMapper.selectPage(page, wrapper).getRecords();
        return posts.stream().map(this::convertToDTO).toList();
    }

    public void likePost(Long postId, User user) {
        postMapper.updateLikeCount(postId, 1);
    }

    public void unlikePost(Long postId, User user) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new ResourceNotFoundException("Post not found");

        if (post.getLikeCount() > 0) {
            postMapper.updateLikeCount(postId, -1);
        }
    }

    public IPage<PostDTO> getPostsByVisibility(VisibilityEnum visibility, Pageable pageable) {
        Page<Post> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getVisibility, visibility.name());

        IPage<Post> page = postMapper.selectPage(mpPage, wrapper);
        return page.convert(this::convertToDTO);
    }

    public IPage<PostDTO> getPendingAuditPosts(Pageable pageable) {
        Page<Post> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getAudited, false);
        return postMapper.selectPage(mpPage, wrapper).convert(this::convertToDTO);
    }

    public void auditPost(Long postId, boolean approved) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new ResourceNotFoundException("Post not found");

        post.setAudited(true);
        post.setApproved(approved);
        post.setAuditTime(LocalDateTime.now());

        postMapper.updateById(post);
    }
}
