package com.example.tastysphere_api.dto.mapper;

import com.example.tastysphere_api.dto.CommentDTO;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.entity.Comment;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PostDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "commentCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0)")
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "isMine", ignore = true)
    @Mapping(target = "images", expression = "java(FormatUrl(post))")
    @Mapping(target = "commentDTOs", source = "comments", qualifiedByName = "mapComments")
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "userAvatar", ignore = true)
    PostDTO toDTO(Post post);

    default PostDTO toDTO(Post post, User user) {
        PostDTO dto = toDTO(post);
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setUserAvatar(user.getAvatar());
        }
        return dto;
    }

    List<PostDTO> toDTOList(List<Post> posts);
// 头像和用户名在服务类中设置
    @Named("mapComments")
    default List<CommentDTO> mapComments(List<Comment> comments) {
        if (comments == null) return new ArrayList<>();
        return commentsToCommentDTOs(comments);
    }

    List<CommentDTO> commentsToCommentDTOs(List<Comment> comments);

    @Named("toDTOWithUserContext")
    default PostDTO toDTOWithUserContext(Post post, Long currentUserId, Set<Long> likedPostIds) {
        PostDTO dto = toDTO(post);
        dto.setIsMine(post.getUserId() != null && post.getUserId().equals(currentUserId));
        dto.setIsLiked(likedPostIds != null && likedPostIds.contains(post.getId()));
        return dto;
    }

    default List<String> FormatUrl(Post post) {
        List<String> images = post.getImages();
        List<String> newImages = new ArrayList<>();
        for (String image : images) {
            if (!image.startsWith("http://") && !image.startsWith("https://")) {
                newImages.add("http://192.168.146.133:888/posts/" + image);
            } else {
                newImages.add(image);
            }
        }
        return newImages;
    }

    @AfterMapping
    default void afterMapping(@MappingTarget PostDTO target) {
        if (target.getImages() == null) target.setImages(new ArrayList<>());
        if (target.getLikeCount() == null) target.setLikeCount(0);
        if (target.getCommentCount() == null) target.setCommentCount(0);
    }
    @Named("toDTOWithUserAndContext")
    default PostDTO toDTOWithUserAndContext(Post post, User user, Long currentUserId, Set<Long> likedPostIds) {
        PostDTO dto = toDTO(post);
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setUserAvatar(user.getAvatar());
            System.out.println("User avatar: " + user.getAvatar());
        }
        dto.setIsMine(currentUserId != null && post.getUserId().equals(currentUserId));
        dto.setIsLiked(likedPostIds != null && likedPostIds.contains(post.getId()));
        return dto;
    }

}
