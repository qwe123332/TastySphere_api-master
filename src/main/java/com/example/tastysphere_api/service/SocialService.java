package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.entity.*;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SocialService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Comment createComment(Long postId, Long parentId, String content, User user) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());
        comment.setContent(content);

        if (parentId != null) {
            comment.setParentCommentId(parentId);
        }

        commentMapper.insert(comment); // 插入数据库，生成 comment.id

        // 通知评论人
        if (parentId != null) {
            Comment parentComment = commentMapper.selectById(parentId);
            if (parentComment != null) {
                // ⚠️ 用 userId 查找 user 对象
                User parentUser = userMapper.selectById(parentComment.getUserId());

                notificationService.createNotification(
                        parentUser,
                        user.getUsername() + " 回复了你的评论",
                        "COMMENT",
                        comment.getId()
                );
            }
        }

        return comment;
    }

    @Transactional
    public void toggleLike(Long postId, User user) {
        Long userId = user.getId();

        boolean exists = likeMapper.selectCount(new QueryWrapper<Like>()
                .eq("post_id", postId)
                .eq("user_id", userId)) > 0;

        if (exists) {
            likeMapper.delete(new QueryWrapper<Like>()
                    .eq("post_id", postId)
                    .eq("user_id", userId));
            postMapper.updateLikeCount(postId, -1);
        } else {
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            likeMapper.insert(like);
            postMapper.updateLikeCount(postId, 1);
        }

        // 通知被点赞用户
        Post post = postMapper.selectById(postId);
        if (post != null && post.getUserId() != null) {
            // ⚠️ 这里使用 userRepository 查询被点赞用户
            User postOwner = userMapper.selectById(post.getUserId());

            if (postOwner != null) {
                notificationService.createNotification(
                        postOwner,
                        user.getUsername() + " 赞了你的帖子",
                        "LIKE",
                        postId
                );
            }
        }
    }

    @Transactional
    public void toggleFollow(User follower, User following) {
        Long followerId = follower.getId();
        Long followingId = following.getId();

        boolean exists = followMapper.selectCount(new QueryWrapper<Follow>()
                .eq("follower_id", followerId)
                .eq("following_id", followingId)) > 0;

        if (exists) {
            followMapper.delete(new QueryWrapper<Follow>()
                    .eq("follower_id", followerId)
                    .eq("following_id", followingId));
        } else {
            Follow follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            followMapper.insert(follow);
        }

        notificationService.createNotification(
                following,
                follower.getUsername() + " 关注了你",
                "FOLLOW",
                followerId
        );
    }

    public org.springframework.data.domain.Page<Comment> getPostComments(Long postId, Pageable pageable) {
        Page<Comment> mpPage = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId);

        Page<Comment> result = commentMapper.selectPage(mpPage, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    public org.springframework.data.domain.Page<Comment> getCommentReplies(Long commentId, Pageable pageable) {
        Page<Comment> mpPage = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_comment_id", commentId);

        Page<Comment> result = commentMapper.selectPage(mpPage, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }
}
