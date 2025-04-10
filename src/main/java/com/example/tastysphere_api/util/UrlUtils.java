package com.example.tastysphere_api.util;

public class UrlUtils {
    public static String resolveAvatarUrl(String avatar) {
        if (avatar == null || avatar.isBlank()) {
            return "http://192.168.146.133:888/avatars/default.png";
        }
        if (!avatar.startsWith("http://") && !avatar.startsWith("https://")) {
            return "http://192.168.146.133:888/avatars/" + avatar;
        }
        return avatar;
    }

    public static String resolveImageUrl(String image) {
        if (image == null || image.isBlank()) return "";
        if (!image.startsWith("http://") && !image.startsWith("https://")) {
            return "http://192.168.146.133:888/posts/" + image;
        }
        return image;
    }
}
