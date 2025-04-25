package com.example.tastysphere_api.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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


    private static final String SECRET_KEY = "JWTSuperSecretKeyJWTSuperSecretKey"; // 替换为你的密钥

    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // sub字段
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}