package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.dto.mapper.UserDtoMapper;
import com.example.tastysphere_api.entity.*;
import com.example.tastysphere_api.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private UserDtoMapper userDtoMapper;

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
                UserDTO parentUser = userDtoMapper.toDTO(userMapper.selectById(parentComment.getUserId()));

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
            UserDTO postOwner =  userDtoMapper.toDTO(userMapper.selectById(post.getUserId()));

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
    public void toggleFollow(User follower, UserDTO following) {
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

    public IPage<CommentDTO> getPostComments(Long postId, Pageable pageable) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId).orderByDesc("created_at");

        // 创建原始分页对象（注意：MyBatis-Plus的Page页码从1开始）
        Page<Comment> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<Comment> commentPage = commentMapper.selectPage(page, wrapper);

        // 创建DTO分页对象，复制分页参数
        Page<CommentDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(commentPage.getCurrent());
        dtoPage.setSize(commentPage.getSize());
        dtoPage.setTotal(commentPage.getTotal());
        dtoPage.setPages(commentPage.getPages());

        // 转换数据：Comment → CommentDTO
        List<CommentDTO> dtoList = commentPage.getRecords().stream()
                .map(comment -> {
                    User user = userMapper.selectById(comment.getUserId());
                    return new CommentDTO(comment, user); // 确保CommentDTO有对应构造函数
                })
                .collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    public IPage<CommentDTO> getCommentReplies(Long commentId, Pageable pageable) {
        // 1. 构建查询条件（查询指定评论的回复）
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentCommentId, commentId)  // 根据父评论ID过滤回复
                .orderByDesc(Comment::getCreatedTime);  // 按创建时间倒序

        // 2. 对齐分页参数（Spring Pageable从0开始，MyBatis-Plus从1开始）
        Page<Comment> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());

        // 3. 执行分页查询
        IPage<Comment> commentPage = commentMapper.selectPage(page, wrapper);

        // 4. 批量查询关联用户信息（优化N+1问题）
        List<Long> userIds = commentPage.getRecords().stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 5. 转换分页对象
        return commentPage.convert(comment -> {
            User user = userMap.getOrDefault(comment.getUserId(), new User());
            return new CommentDTO(
                    comment.getId(),
                    comment.getContent(),
                    comment.getCreatedTime(),
                    user.getUsername(),        // 用户名字段
                    user.getAvatar()        // 用户头像字段
            );
        });
    }
}
