package com.example.tastysphere_api.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.SystemNotificationQueryDTO;
import com.example.tastysphere_api.entity.SystemNotification;
import com.example.tastysphere_api.service.SystemNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications")
public class SystemNotificationController {

    @Autowired
    private SystemNotificationService systemNotificationService;

    // 新增系统通知
    @PostMapping
    public boolean createNotification(@RequestBody SystemNotification notification) {
        return systemNotificationService.createNotification(notification);
    }

    // 删除通知
    @DeleteMapping("/{id}")
    public boolean deleteNotification(@PathVariable Long id) {
        return systemNotificationService.deleteNotification(id);
    }

    // 查询单个通知
    @GetMapping("/{id}")
    public SystemNotification getNotification(@PathVariable Long id) {
        return systemNotificationService.getNotification(id);
    }

    // 统一条件分页查询
    @PostMapping("/search")
    public Page<SystemNotification> searchNotifications(@RequestBody SystemNotificationQueryDTO queryDTO) {
        return systemNotificationService.searchNotifications(queryDTO);
    }
    // 更新系统通知
    @PutMapping("/{id}")
    public boolean updateNotification(@PathVariable Long id, @RequestBody SystemNotification notification) {
        notification.setId(id);
        return systemNotificationService.updateById(notification);
    }

}
