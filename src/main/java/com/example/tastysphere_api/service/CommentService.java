package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.dto.mapper.CommentDtoMapper;
import com.example.tastysphere_api.entity.Comment;
import com.example.tastysphere_api.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    public List<CommentDTO> getCommentsByPost(Long postId) {
        // 查询顶层评论（无父评论）
        List<Comment> topLevelComments = commentMapper.selectList(new QueryWrapper<Comment>()
                .eq("post_id", postId)
                .isNull("parent_comment_id"));

        // 查询所有子评论
        List<Long> parentIds = topLevelComments.stream()
                .map(Comment::getId).collect(Collectors.toList());

        List<Comment> allReplies = parentIds.isEmpty()
                ? Collections.emptyList()
                : commentMapper.selectList(new QueryWrapper<Comment>()
                .in("parent_comment_id", parentIds));

        Map<Long, List<Comment>> repliesMap = allReplies.stream()
                .collect(Collectors.groupingBy(Comment::getParentCommentId));

        return topLevelComments.stream()
                .map(comment -> {
                    CommentDTO dto = commentDtoMapper.toDTO(comment);
                    dto.setReplies(commentDtoMapper.toDTOList(
                            repliesMap.getOrDefault(comment.getId(), Collections.emptyList())));
                    return dto;
                })
                .toList();
    }

    public IPage<CommentDTO> getPostComments(Long postId, long current, long size) {
        // 1. 构建分页对象（页码从1开始）
        Page<Comment> mpPage = new Page<>(current, size);

        // 2. 构建Lambda查询条件
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery(Comment.class)
                .eq(Comment::getPostId, postId)
                .isNull(Comment::getParentCommentId);

        // 3. 查询顶级评论分页数据
        IPage<Comment> topLevelPage = commentMapper.selectPage(mpPage, wrapper);
        List<Comment> topLevelComments = topLevelPage.getRecords();

        // 4. 批量查询回复（优化N+1问题）
        List<Long> parentIds = topLevelComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> repliesMap = parentIds.isEmpty()
                ? Collections.emptyMap()
                : commentMapper.selectList(Wrappers.lambdaQuery(Comment.class)
                        .in(Comment::getParentCommentId, parentIds))
                .stream()
                .collect(Collectors.groupingBy(Comment::getParentCommentId));

        // 5. 转换分页对象
        return topLevelPage.convert(comment -> {
            CommentDTO dto = commentDtoMapper.toDTO(comment);
            dto.setReplies(commentDtoMapper.toDTOList(
                    repliesMap.getOrDefault(comment.getId(), Collections.emptyList())));
            return dto;
        });
    }

    public IPage<Comment> getCommentReplies(Long parentCommentId, long current, long size) {
        // MyBatis-Plus 分页对象（页码从1开始）
        Page<Comment> mpPage = new Page<>(current, size);
        // 构建Lambda查询条件（推荐类型安全写法）
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery(Comment.class)
                .eq(Comment::getParentCommentId, parentCommentId);
        // 执行分页查询
        return commentMapper.selectPage(mpPage, wrapper);
    }
}