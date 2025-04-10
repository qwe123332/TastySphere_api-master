package com.example.tastysphere_api.security;

import com.example.tastysphere_api.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final Key key; // 用于签名的密钥
    private final long jwtExpirationInMs; // Token 过期时间

    public JwtTokenProvider(
            @Value("${app.jwtSecret:JWTSuperSecretKeyJWTSuperSecretKey}") String jwtSecret,
            @Value("${app.jwtExpirationInMs:86400000}") long jwtExpirationInMs) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes()); // 将密钥字符串转换为 Key 对象
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     * 生成 JWT Token。
     *
     * @param email 用户邮箱（作为 Token 的主题）
     * @return 生成的 JWT Token
     */
    public String generateToken(String email, List<Role> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs); // 计算过期时间
        // 将 roles 转换为角色名称列表
        List<String> roleNames = roles.stream()
                .map(Role::getName) // 假设 Role 类有一个 getName() 方法
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(email) // 设置主题
                .claim("roles", roleNames)
                .setIssuedAt(now) // 设置签发时间
                .setExpiration(expiryDate) // 设置过期时间
                .signWith(key, SignatureAlgorithm.HS256) // 使用密钥签名
                .compact(); // 生成 Token
    }

    /**
     * 从 JWT Token 中解析用户名（邮箱）。
     *
     * @param token JWT Token
     * @return 用户名（邮箱）
     */
    public String getUsernameFromJWT(String token) {
        return parseToken(token).getSubject(); // 解析 Token 并获取主题
    }


    public List<String> getRolesFromJWT(String token) {
        Claims claims = parseToken(token); // 解析 Token
        return claims.get("roles", List.class); // 获取角色信息
    }

    /**
     * 验证 JWT Token 是否有效。
     *
     * @param authToken JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String authToken) {
        try {
            parseToken(authToken); // 解析 Token，如果无效会抛出异常
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    /**
     * 解析 JWT Token 并返回 Claims。
     *
     * @param token JWT Token
     * @return Claims 对象
     */
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 设置签名密钥
                .build()
                .parseClaimsJws(token) // 解析 Token
                .getBody(); // 获取 Claims
    }
}