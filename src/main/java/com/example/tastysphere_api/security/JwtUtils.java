package com.example.tastysphere_api.security;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
@Component
public class JwtUtils {

    /**
     * 从请求中提取 JWT Token。
     *
     * @param request HTTP 请求
     * @return JWT Token，如果不存在则返回 null
     */
    public static String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // 获取 Authorization 头
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 提取 Token（去掉 "Bearer " 前缀）
        }
        return null;
    }
}