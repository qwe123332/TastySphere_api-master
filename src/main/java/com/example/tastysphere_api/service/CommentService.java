package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    public org.springframework.data.domain.Page<CommentDTO> getPostComments(Long postId, org.springframework.data.domain.Pageable pageable) {
        // 使用 MP 的分页
        Page<Comment> mpPage = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId).isNull("parent_comment_id");

        Page<Comment> topLevelPage = commentMapper.selectPage(mpPage, wrapper);
        List<Comment> topLevelComments = topLevelPage.getRecords();

        List<Long> parentIds = topLevelComments.stream().map(Comment::getId).toList();

        Map<Long, List<Comment>> repliesMap = parentIds.isEmpty()
                ? Collections.emptyMap()
                : commentMapper.selectList(new QueryWrapper<Comment>()
                        .in("parent_comment_id", parentIds))
                .stream()
                .collect(Collectors.groupingBy(Comment::getParentCommentId));

        List<CommentDTO> dtoList = topLevelComments.stream()
                .map(c -> {
                    CommentDTO dto = commentDtoMapper.toDTO(c);
                    dto.setReplies(commentDtoMapper.toDTOList(
                            repliesMap.getOrDefault(c.getId(), Collections.emptyList())));
                    return dto;
                })
                .toList();

        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, topLevelPage.getTotal());
    }

    public org.springframework.data.domain.Page<Comment> getCommentReplies(Long parentCommentId, org.springframework.data.domain.Pageable pageable) {
        Page<Comment> mpPage = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_comment_id", parentCommentId);

        Page<Comment> result = commentMapper.selectPage(mpPage, wrapper);
        return new org.springframework.data.domain.PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }
}