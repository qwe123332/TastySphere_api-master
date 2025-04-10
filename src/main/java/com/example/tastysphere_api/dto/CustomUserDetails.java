package com.example.tastysphere_api.dto;

import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data


public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRoles().toString())); // 角色转换成 Spring Security 需要的权限
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 使用 email 作为用户名
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 账户不过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 账户不锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 凭据不过期
    }

    public List<Role> getroles() {
        return user.getRoles();
    }


    public Long getUserId() {
        return user.getId();  // 获取 User ID
    }
}
