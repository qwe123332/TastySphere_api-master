package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.entity.AuditLog;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.BusinessException;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.mapper.AuditLogMapper;
import com.example.tastysphere_api.mapper.PostMapper;
import com.example.tastysphere_api.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private NotificationService notificationService;

    /** 获取所有审计日志 */
    public Page<AuditLog> getAuditLogs(Page<AuditLog> page) {
        // 使用MyBatis Plus的selectPage方法[2,7](@ref)
        return auditLogMapper.selectPage(page, null);
    }
    /** 系统总览统计 */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalPosts", postMapper.selectCount(null));
        stats.put("activeUsers", userMapper.selectCount(new QueryWrapper<User>().eq("active", true)));
        return stats;
    }

    /** 系统详细统计（含今日新增） */
    public Map<String, Object> getDetailedStatistics() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0);

        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalPosts", postMapper.selectCount(null));
        stats.put("activeUsers", userMapper.selectCount(new QueryWrapper<User>().eq("active", true)));
        stats.put("newUsersToday", userMapper.countSince(today));
        stats.put("newPostsToday", postMapper.countSince(today));
        stats.put("pendingAudit", postMapper.selectCount(new QueryWrapper<Post>().eq("audited", false)));
        stats.put("approvedPosts", postMapper.selectCount(new QueryWrapper<Post>().eq("audited", true).eq("approved", true)));

        return stats;
    }
    @Transactional(rollbackFor = Exception.class)
    public void auditPost(Long postId, boolean approved, User admin, String reason) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new ResourceNotFoundException("Post not found");

        try {
            post.setAudited(true);
            post.setApproved(approved);
            post.setAuditTime(LocalDateTime.now());
            postMapper.updateById(post);

            // 插入审计日志
            AuditLog logEntry = new AuditLog();
            logEntry.setAdminId(admin.getId());
            logEntry.setActionType("POST_AUDIT");
            logEntry.setTargetId(postId);
            logEntry.setActionDetail("Post " + (approved ? "approved" : "rejected") + ". Reason: " + reason);
            auditLogMapper.insert(logEntry);

            // 获取发帖人用户
            Long authorId = post.getUserId();
            if (authorId != null) {
                User postUser = userMapper.selectById(authorId);


                if (postUser != null) {
                    notificationService.sendNotification(
                            postUser,
                            "你的帖子已被" + (approved ? "通过" : "驳回") + "，原因：" + reason,
                            "POST_AUDIT"
                    );
                }
            }

        } catch (Exception e) {
            log.error("审核帖子失败: {}", e.getMessage());
            throw new BusinessException("审核操作失败: " + e.getMessage());
        }
    }

    /** 用户分页查询 */
    public org.springframework.data.domain.Page<User> getUsers(int page, int size) {
        Page<User> mpPage = userMapper.selectPage(new Page<>(page, size), null);
        return new PageImpl<>(mpPage.getRecords(), org.springframework.data.domain.PageRequest.of(page, size), mpPage.getTotal());
    }

    /** 更新用户状态（封禁/解封） */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, boolean active) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new ResourceNotFoundException("User not found with id: " + userId);

        try {
            user.setActive(active);
            userMapper.updateById(user);

            AuditLog logEntry = new AuditLog();
            logEntry.setActionType("USER_STATUS_UPDATE");
            logEntry.setTargetId(userId);
            logEntry.setActionDetail("User status updated to: " + (active ? "active" : "inactive"));
            auditLogMapper.insert(logEntry);

            String msg = active ? "您的账号已被激活" : "您的账号已被禁用";
            notificationService.sendNotification(user, msg, "ACCOUNT_STATUS");

            log.info("用户 {} 状态更新为 {}", userId, active);
        } catch (Exception e) {
            log.error("更新用户状态失败: {}", e.getMessage());
            throw new BusinessException("状态更新失败: " + e.getMessage());
        }
    }

    /** 获取待审核帖子分页 */
    public org.springframework.data.domain.Page<Post> getPendingPosts(Pageable pageable) {
        Page<Post> mpPage = postMapper.selectPage(
                new Page<>(pageable.getPageNumber(), pageable.getPageSize()),
                new QueryWrapper<Post>().eq("audited", false)
        );
        return new PageImpl<>(mpPage.getRecords(), pageable, mpPage.getTotal());
    }

    /** 获取系统运行资源指标 */
    public Map<String, Object> getSystemMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("cpu_usage", getCpuUsage());
            metrics.put("memory_usage", getMemoryUsage());
            metrics.put("disk_usage", getDiskUsage());
            return metrics;
        } catch (Exception e) {
            log.error("获取系统指标失败: {}", e.getMessage());
            throw new BusinessException("获取系统指标失败");
        }
    }

    private double getCpuUsage() {
        OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return os.getSystemLoadAverage();
    }

    private long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private double getDiskUsage() {
        File root = new File("/");
        return (double) (root.getTotalSpace() - root.getFreeSpace()) / root.getTotalSpace() * 100;
    }


}
