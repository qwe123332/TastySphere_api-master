package com.example.tastysphere_api.controller.admin;

import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.request.UserLoginRequest;
import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest) {
        try {
            System.out.println("Admin login attempt: " + loginRequest.getEmail() + ", " + loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<Role> roles = userDetails.getroles();

            boolean isAdmin = roles.stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
            if (!isAdmin) {
                return ResponseEntity.status(403).body("您无后台访问权限");
            }

            String token = tokenProvider.generateToken(authentication.getName(), roles);

            // 构建用户信息返回
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userDetails.getUserId());
            userInfo.put("username", userDetails.getUsername());
            userInfo.put("email", userDetails.getUser().getEmail());
            userInfo.put("roles", roles.stream().map(Role::getName).toList());

            // 总响应
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userInfo);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("管理员登录失败：用户名或密码错误");
        }
    }

    /**
     * 管理员退出登录（加入 token 拉黑）
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("缺少有效的Authorization头");
        }

        String token = authHeader.substring(7);
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            long ttl = expiration.getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(token, "invalidated", ttl, TimeUnit.MILLISECONDS);
            }

            SecurityContextHolder.clearContext();
            return ResponseEntity.ok("登出成功");

        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(401).body("令牌已过期");
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("无效令牌");
        }
    }
}
