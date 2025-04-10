package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.request.UserLoginRequest;
import com.example.tastysphere_api.dto.request.UserRegisterRequest;
import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.security.JwtTokenProvider;
import com.example.tastysphere_api.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}") // 从配置读取密钥
    private String jwtSecret;
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未认证");
        }

        // 获取当前用户的权限信息
        List<String> authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("roles", authorities);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserLoginRequest loginRequest) {
        try {
            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            Object principal = authentication.getPrincipal();
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            List<Role> list = userDetails.getroles();
            // 认证成功后生成 JWT Token
            String token = tokenProvider.generateToken(authentication.getName(), list);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest registerRequest) {
        // 处理注册逻辑
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("邮箱已被注册");
        }
      User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhone());
        user.setStatus(registerRequest.getStatus());
        user.setStatus(registerRequest.getStatus());
        // 头像字段这里不做设置，可留空

        // 设置创建和更新时间（这里简单设置为当前时间）
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedTime(now);
        user.setUpdatedTime(now);
        // 默认激活状态
        user.setActive(true);
        userService.createUser(user);
        return ResponseEntity.ok("注册成功");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {

        // 1. 从请求头中获取JWT令牌
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("缺少有效的Authorization头");
        }

        String token = authHeader.substring(7); // 去掉"Bearer "前缀

        try {
            // 2. 解析令牌获取声明信息（包括过期时间）
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret) // 使用你的密钥
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 3. 计算剩余有效时间
            Date expiration = claims.getExpiration();
            long currentTimeMillis = System.currentTimeMillis();
            long ttl = expiration.getTime() - currentTimeMillis;

            // 4. 将令牌加入黑名单（使用Redis存储）
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        token,
                        "invalidated",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }

            // 5. 清理安全上下文
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok("登出成功");
        } catch (ExpiredJwtException ex) {
            // 令牌已过期的情况处理
            return ResponseEntity.status(401).body("令牌已过期");
        } catch (JwtException | IllegalArgumentException ex) {
            // 无效令牌的情况处理
            return ResponseEntity.badRequest().body("无效令牌");
        }
    }
}
