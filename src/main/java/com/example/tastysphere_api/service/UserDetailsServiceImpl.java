package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.mapper.RoleMapper;
import com.example.tastysphere_api.mapper.UserMapper;
import com.example.tastysphere_api.dto.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public UserDetailsServiceImpl(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        // ✅ 加载角色列表

        List<Role> roles = roleMapper.findRolesByUserId(user.getId());

        // ✅ 手动设置
        user.setRoles(roles);
        return new CustomUserDetails(user);
    }



}
