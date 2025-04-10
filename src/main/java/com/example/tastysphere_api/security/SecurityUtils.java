package com.example.tastysphere_api.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 * 安全工具类，提供认证上下文设置功能。
 */
public class SecurityUtils {

    /**
     * 设置认证上下文。
     *
     * @param userDetails 用户详情
     * @param request     HTTP 请求
     */
    public static void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // 创建认证对象
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 设置请求详情
        SecurityContextHolder.getContext().setAuthentication(authentication); // 设置认证上下文
    }
}