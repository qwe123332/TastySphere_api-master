package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.entity.Notification;
import com.example.tastysphere_api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public Page<Notification> getNotifications(
            @AuthenticationPrincipal CustomUserDetails user,
            Pageable pageable) {
        // Extract page number and page size from Pageable
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        return notificationService.getUserNotifications(user.getUser().getId(), page, size);
    }

    @PostMapping("/{notificationId}/read")
    public void markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails user) {
        notificationService.markAsRead(notificationId, user.getUser().getId());
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(@AuthenticationPrincipal CustomUserDetails user) {
        return notificationService.getUnreadCount(user.getUser().getId());
    }
}