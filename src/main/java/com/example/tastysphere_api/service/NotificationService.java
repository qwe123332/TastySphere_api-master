package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.entity.Notification;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.mapper.NotificationMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void createNotification(User user, String content, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(user.getId()); // Set userId directly
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notificationMapper.insert(notification); // Use MyBatis-Plus insert method
    }

    public Page<Notification> getUserNotifications(Long userId, int page, int size) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("created_at");

        Page<Notification> notificationPage = new Page<>(page, size);
        return notificationMapper.selectPage(notificationPage, queryWrapper); // Use selectPage for pagination
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setRead(true);
            notificationMapper.updateById(notification); // Use updateById to mark as read
        }
    }

    public long getUnreadCount(Long userId) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("is_read", false);
        return notificationMapper.selectCount(queryWrapper); // Use selectCount for unread notifications count
    }

    public void sendNotification(User user, String message, String type) {
        String key = "notifications:" + user.getId();
        String notification = String.format("{\"type\":\"%s\",\"message\":\"%s\",\"time\":\"%s\"}",
                type, message, java.time.LocalDateTime.now());

        // Using Redis list to store notifications
        redisTemplate.opsForList().leftPush(key, notification);
        // Limit notification count, keeping the latest 100 notifications
        redisTemplate.opsForList().trim(key, 0, 99);
    }

    public void sendSystemNotification(String message) {
        // System-level notification, stored in the system notifications queue
        String key = "notifications:system";
        String notification = String.format("{\"type\":\"SYSTEM\",\"message\":\"%s\",\"time\":\"%s\"}",
                message, java.time.LocalDateTime.now());
        redisTemplate.opsForList().leftPush(key, notification);
    }
}
