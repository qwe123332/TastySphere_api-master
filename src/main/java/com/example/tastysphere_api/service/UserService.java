package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.tastysphere_api.dto.NotificationDTO;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.dto.mapper.UserDtoMapper;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PostService postService;
    private final UserDtoMapper userDtoMapper;
    private final ApplicationContext applicationContext;

    @Autowired
    public UserService(UserMapper userMapper, PostService postService, UserDtoMapper userDtoMapper,
                       ApplicationContext applicationContext) {
        this.userMapper = userMapper;
        this.postService = postService;
        this.userDtoMapper = userDtoMapper;
        this.applicationContext = applicationContext;
    }

    /**
     * Creates a new user after validation
     */
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        validateUser(user);
        userMapper.insert(user);
        return user;
    }

    /**
     * Updates user profile with proper transaction and cache management
     */
    @CacheEvict(value = "users", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        User user = findUserOrThrow(userId);

        // Update user fields from DTO
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        // Update other fields as needed

        userMapper.updateById(user);
        return userDtoMapper.toDTO(user);
    }

    /**
     * Gets posts by user with pagination
     */
    @Transactional(readOnly = true)
    public IPage<PostDTO> getUserPosts(Long userId, PageRequest pageRequest) {
        User user = findUserOrThrow(userId);
        return postService.getPostsByUser(user.getId(), pageRequest);
    }

    /**
     * Gets all users (consider adding pagination for large datasets)
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    /**
     * Gets user by ID with caching
     */
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * Deletes a user
     */
    @CacheEvict(value = "users", key = "#id")
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    /**
     * Gets user profile by ID with caching
     */
    @Cacheable(value = "userProfiles", key = "#userId", unless = "#result == null")
    @Transactional(readOnly = true)
    public UserDTO getUserProfile(Long userId) {
        User user = findUserOrThrow(userId);
        return userDtoMapper.toDTO(user);
    }

    /**
     * Follows another user
     */
    @Transactional(rollbackFor = Exception.class)
    public void followUser(Long userId, Long targetUserId) {
        Assert.isTrue(!userId.equals(targetUserId), "Users cannot follow themselves");

        User user = findUserOrThrow(userId);
        User targetUser = findUserOrThrow(targetUserId);

        // Implement following logic here
        // For example:
        // userFollowRepository.save(new UserFollow(user.getId(), targetUser.getId()));
    }

    /**
     * Unfollows a user
     */
    @Transactional(rollbackFor = Exception.class)
    public void unfollowUser(Long userId, Long targetUserId) {
        User user = findUserOrThrow(userId);
        User targetUser = findUserOrThrow(targetUserId);

        // Implement unfollowing logic here
        // For example:
        // userFollowRepository.deleteByUserIdAndTargetUserId(userId, targetUserId);
    }

    /**
     * Gets notifications for a user
     */
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotifications(Long userId, PageRequest pageRequest) {
        findUserOrThrow(userId);

        // Implement notification retrieval logic
        // For example:
        // return notificationRepository.findByUserId(userId, pageRequest)
        //     .map(notificationMapper::toDTO);

        return null; // Replace with actual implementation
    }

    /**
     * Marks a notification as read
     */
    @Transactional(rollbackFor = Exception.class)
    public void markNotificationAsRead(Long notificationId, Long userId) {
        findUserOrThrow(userId);

        // Implement notification marking logic
        // For example:
        // Notification notification = notificationRepository.findById(notificationId)
        //     .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        // if (!notification.getUserId().equals(userId)) {
        //     throw new AccessDeniedException("Cannot access this notification");
        // }
        // notification.setRead(true);
        // notificationRepository.save(notification);
    }

    /**
     * Checks if a username exists
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userMapper.selectCount(new QueryWrapper<User>().eq("username", username)) > 0;
    }

    /**
     * Checks if an email exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userMapper.selectCount(new QueryWrapper<User>().eq("email", email)) > 0;
    }

    /**
     * Helper method to validate user data before creation
     */
    private void validateUser(User user) {
        Assert.notNull(user, "User cannot be null");
        Assert.hasText(user.getUsername(), "Username is required");
        Assert.hasText(user.getEmail(), "Email is required");

        // Add more validation as needed
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    /**
     * Helper method to find a user by ID or throw an exception
     */
    private User findUserOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return user;
    }


}