package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.request.NotificationRequest;
import com.example.tastysphere_api.entity.Notification;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.mapper.NotificationMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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

    // 发送系统通知
    public void sendSystemNotification(NotificationRequest request) {
        // 系统级通知存入 Redis 队列
        String key = "notifications:system";

        // 如果没有设置通知时间，默认使用当前时间
        if (request.getTime() == null) {
            request.setTime(LocalDateTime.now());
        }

        // 构建通知内容，增加类型、时间等字段
        String notification = String.format("{\"type\":\"%s\",\"message\":\"%s\",\"time\":\"%s\"}",
                request.getType(), request.getContent(), request.getTime());

        try {
            // 将通知添加到 Redis 队列中
            redisTemplate.opsForList().leftPush(key, notification);

            // 可选：设置通知的过期时间（例如 30 天）
            redisTemplate.expire(key, 30, TimeUnit.DAYS);
        } catch (Exception e) {
            // 错误处理
            System.err.println("Error sending system notification: " + e.getMessage());
        }
    }


    public IPage<Notification> getUserNotifications(Long userId, int page, int size) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("created_at");

        IPage<Notification> notificationPage = new Page<>(page, size);
        return notificationMapper.selectPage(notificationPage, queryWrapper);
    }

}
