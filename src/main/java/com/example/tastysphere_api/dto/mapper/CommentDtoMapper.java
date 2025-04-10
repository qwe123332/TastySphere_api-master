package com.example.tastysphere_api.dto.mapper;

import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "parentCommentId", source = "parentCommentId")
    // replies is set manually in the service class, so we can ignore it here
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "userAvatar", source = "userAvatar")
    @Mapping(target = "likeCount", source = "likeCount")
    @Mapping(target = "createdAt", source = "createdTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "content", source = "content")
    CommentDTO toDTO(Comment comment);

    List<CommentDTO> toDTOList(List<Comment> comments);
}