package com.example.tastysphere_api.dto.mapper;

import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")

public interface UserDtoMapper {
    @Mapping(target = "followersCount", expression = "java(getFollowersCount(user))")
    @Mapping(target = "followingCount", expression = "java(getFollowingCount(user))")
    @Mapping(target = "avatar", expression = "java(formatAvatarUrl(user))")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "active", source = "active")


    UserDTO toDTO(User user);

    default int getFollowersCount(User user) {
        // 实现获取关注者数量的逻辑
        return 1; // 实际实现中替换为真实逻辑
    }
    default int getFollowingCount(User user) {
        // 实现获取关注数量的逻辑
        return 2; // 实际实现中替换为真实逻辑
    }
    default String formatAvatarUrl(User user) {
   String avatar = user.getAvatar();
        if (avatar == null || avatar.isEmpty()) {
            return "/default-avatar.png"; // 提供一个默认头像的 URL
        }
        if (!avatar.startsWith("http://") && !avatar.startsWith("https://")) {
            // 如果 avatar 不是一个完整的 URL，则假设它是一个相对路径
            // 你需要根据你的实际情况调整这里的 URL 前缀
            return "http://192.168.146.133:888/avatars/" + avatar;
        }
        return avatar; // 如果 avatar 已经是一个完整的 URL，则直接返回
    }

}
