package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.NotificationDTO;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.dto.mapper.UserDtoMapper;
import com.example.tastysphere_api.dto.response.CommonResponse;
import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PostService postService;
    private final UserDtoMapper userDtoMapper;
    private final RoleService roleService;
    private final ApplicationContext applicationContext;

    @Autowired
    public UserService(UserMapper userMapper, PostService postService, UserDtoMapper userDtoMapper, RoleService roleService, ApplicationContext applicationContext) {
        this.userMapper = userMapper;
        this.postService = postService;
        this.userDtoMapper = userDtoMapper;
        this.roleService = roleService;
        this.applicationContext = applicationContext;
    }

    public  String getUserIdByEmail(String email) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("email", email));
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        return user.getId().toString();


    }

    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        validateUser(user);
        userMapper.insert(user);
        return user;
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        UserDTO user = findUserOrThrow(userId);
        if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        userMapper.updateById((Collection<User>) user);
        return user;
    }

    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userMapper.selectById(id);
        return userDtoMapper.toDTO(user);
    }

    @Cacheable(value = "userProfiles", key = "#userId", unless = "#result == null")
    @Transactional(readOnly = true)
    public UserDTO getUserProfile(Long userId) {
        UserDTO user = findUserOrThrow(userId);
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void followUser(Long userId, Long targetUserId) {
        Assert.isTrue(!userId.equals(targetUserId), "Users cannot follow themselves");
        findUserOrThrow(userId);
        findUserOrThrow(targetUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unfollowUser(Long userId, Long targetUserId) {
        findUserOrThrow(userId);
        findUserOrThrow(targetUserId);
    }

    @Transactional(readOnly = true)
    public IPage<NotificationDTO> getNotifications(Long userId, int page, int size) {
        findUserOrThrow(userId);

        Page<NotificationDTO> mpPage = new Page<>(page, size);
        List<NotificationDTO> records = new ArrayList<>(); // TODO: Replace with actual query
        mpPage.setRecords(records);
        mpPage.setTotal(0); // TODO: Replace with actual count

        return mpPage;
    }

    @Transactional(rollbackFor = Exception.class)
    public void markNotificationAsRead(Long notificationId, Long userId) {
        findUserOrThrow(userId);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userMapper.selectCount(new QueryWrapper<User>().eq("username", username)) > 0;
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userMapper.selectCount(new QueryWrapper<User>().eq("email", email)) > 0;
    }

    private void validateUser(User user) {
        Assert.notNull(user, "User cannot be null");
        Assert.hasText(user.getUsername(), "Username is required");
        Assert.hasText(user.getEmail(), "Email is required");

        if (existsByUsername(user.getUsername())) throw new IllegalArgumentException("Username already exists");
        if (existsByEmail(user.getEmail())) throw new IllegalArgumentException("Email already exists");
    }

    private UserDTO findUserOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new ResourceNotFoundException("User not found with id: " + userId);
        return userDtoMapper.toDTO(user);
    }

    public void blockUser(Long blockedUserId) {}

    public void unblockUser(Long blockedUserId) {}

    public CommonResponse searchUsers(String keyword, int page, int pageSize) {
        if (page < 0 || pageSize <= 0) {
            return new CommonResponse(400, "Invalid pagination parameters", Map.of("items", Collections.emptyList(), "total", 0));
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(User::getUsername, keyword).or().like(User::getEmail, keyword);
        }

        IPage<User> pageResult = userMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return new CommonResponse(200, "Search successful", Map.of("items", pageResult.getRecords()), pageResult.getTotal());
    }


    @CacheEvict(value = {"users", "userProfiles"}, key = "#userId")
    public void updateUserStatus(Long id, boolean active) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        user.setActive(active);
        userMapper.updateById(user);
    }



    public IPage<User> getAllUsers(IPage<User> page, String search) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (search != null && !search.trim().isEmpty()) {
            queryWrapper.lambda().like(User::getUsername, search.trim());
        }
        return userMapper.selectPage(page, queryWrapper);
    }

    public User updateUser(Long id, User user) {
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        if (user.getUsername() != null) existingUser.setUsername(user.getUsername());
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
        userMapper.updateById(existingUser);
        return existingUser;
    }

    public Role getRoleByName(String role) {
        Role roleEntity = roleService.getRoleByName(role);
        if (roleEntity == null) {
            throw new ResourceNotFoundException("Role not found with name: " + role);
        }
        return roleEntity;
    }

    public User getOne(QueryWrapper<User> queryWrapper) {
        return userMapper.selectOne(queryWrapper);
    }

    public void updateByuserId(UserDTO user) {
        User userEntity = userMapper.selectById(user.getId());
        if (userEntity == null) {
            throw new ResourceNotFoundException("User not found with id: " + user.getId());
        }
        if (user.getUsername() != null) userEntity.setUsername(user.getUsername());
        if (user.getEmail() != null) userEntity.setEmail(user.getEmail());
        userMapper.updateById(userEntity);
    }
}
