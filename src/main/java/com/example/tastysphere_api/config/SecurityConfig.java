package com.example.tastysphere_api.config;

import com.example.tastysphere_api.security.JwtAuthenticationEntryPoint;
import com.example.tastysphere_api.security.JwtAuthenticationFilter;
import com.example.tastysphere_api.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final UserDetailsServiceImpl userDetailsService;
    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler,
                          UserDetailsServiceImpl userDetailsService) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //打印出来的是一个SecurityFilterChain

        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/login").permitAll()
                        .requestMatchers("/api/merchant/login").permitAll()
                        .requestMatchers("/ws/**").permitAll() // ✅ 放行 WebSocket 握手请求

                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts").permitAll() // 允许未登录用户查看帖子
                        .requestMatchers(HttpMethod.GET, "/api/posts/{postId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/{postId}/comments").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")  // 新增：仅允许 ROLE_ADMIN 访问
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // 密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 配置 AuthenticationManager，使用自定义 UserDetailsService
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }
}
