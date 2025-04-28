package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.mapper.PostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RecommendationService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public List<Post> getRecommendedPosts(User user) {
        Set<String> recentCategories = getUserRecentCategories(user.getId());

        List<Post> followingPosts = findFollowingApprovedPosts(getUserFollowingIds(user.getId()), 10);
        List<Post> trendingPosts = getTrendingPosts();

        Set<Post> recommendedPosts = new LinkedHashSet<>();
        recommendedPosts.addAll(followingPosts);
        recommendedPosts.addAll(trendingPosts);

        return new ArrayList<>(recommendedPosts);
    }

    private List<Post> findFollowingApprovedPosts(List<Long> userIds, int limit) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyList();

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Post::getUserId, userIds)
                .eq(Post::getApproved, true)
                .eq(Post::getAudited, true)
                .orderByDesc(Post::getCreatedTime);


        return postMapper.selectPage(new Page<>(1, limit), wrapper).getRecords();
    }

    private Set<String> getUserRecentCategories(Long userId) {
        String key = "user:categories:" + userId;
        Set<String> categories = redisTemplate.opsForZSet().range(key, 0, -1);
        return categories;
    }

    private List<Long> getUserFollowingIds(Long userId) {
        String key = "user:following:" + userId;
        Set<String> rawIds = redisTemplate.opsForSet().members(key);

        return rawIds.stream()
                .map(id -> {
                    try {
                        return Long.parseLong(id);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid userId in Redis: {}", id);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Post> getTrendingPosts() {
        String key = "trending:posts";
        Set<String> postIds = redisTemplate.opsForZSet().reverseRange(key, 0, 9);
        if (postIds.isEmpty()) return List.of();

        return postIds.stream()
                .map(id -> {
                    try {
                        return Optional.ofNullable(postMapper.selectById(Long.parseLong(id)));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid post ID in trending: {}", id);
                        return Optional.<Post>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public void recordUserView(Long userId, PostDTO post) {
        try {
            redisTemplate.opsForZSet().incrementScore("trending:posts", post.getId().toString(), 1);
            if (post.getCategory() != null) {
                redisTemplate.opsForZSet().incrementScore("user:categories:" + userId, post.getCategory(), 1);
            }
        } catch (Exception e) {
            log.error("Failed to record user view: {}", e.getMessage());
        }
    }
}
