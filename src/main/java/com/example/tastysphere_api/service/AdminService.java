package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.request.ReportReviewRequest;
import com.example.tastysphere_api.entity.AuditLog;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.PostReport;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.BusinessException;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.mapper.AuditLogMapper;
import com.example.tastysphere_api.mapper.PostMapper;
import com.example.tastysphere_api.mapper.PostReportMapper;
import com.example.tastysphere_api.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final AuditLogMapper auditLogMapper;
    private final NotificationService notificationService;
    private final PostReportMapper postReportMapper;

    public AdminService(UserMapper userMapper, PostMapper postMapper, AuditLogMapper auditLogMapper,
                        NotificationService notificationService, PostReportMapper postReportMapper) {
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.auditLogMapper = auditLogMapper;
        this.notificationService = notificationService;
        this.postReportMapper = postReportMapper;
    }

    /**
     * 获取所有审计日志
     */
    public IPage<AuditLog> getAuditLogs(Page<AuditLog> page) {
        // 使用MyBatis Plus的selectPage方法[2,7](@ref)
        return auditLogMapper.selectPage(page, null);
    }

    /**
     * 系统总览统计
     */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalPosts", postMapper.selectCount(null));
        stats.put("activeUsers", userMapper.selectCount(new QueryWrapper<User>().eq("active", true)));
        return stats;
    }

    /**
     * 系统详细统计（含今日新增）
     */
    public Map<String, Object> getDetailedStatistics() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0);

        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalPosts", postMapper.selectCount(null));

        stats.put("activeUsers", userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getActive, true)
        ));

        stats.put("newUsersToday", userMapper.countSince(today));
        stats.put("newPostsToday", postMapper.countSince(today));

        stats.put("pendingAudit", postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getAudited, false)
        ));

        stats.put("approvedPosts", postMapper.selectCount(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getAudited, true)
                        .eq(Post::getApproved, true)
        ));

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


    /**
     * 更新用户状态（封禁/解封）
     */
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

    /**
     * 获取待审核帖子分页
     */
    public IPage<Post> getPendingPosts(long current, long size) {
        // MyBatis-Plus分页对象（页码从1开始）
        Page<Post> page = new Page<>(current, size);

        // 构建Lambda查询条件（推荐类型安全写法）
        LambdaQueryWrapper<Post> wrapper = Wrappers.<Post>lambdaQuery()
                .eq(Post::getAudited, false);

        // 执行分页查询
        return postMapper.selectPage(page, wrapper);
    }

    /**
     * 获取系统运行资源指标
     */
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


    public PostReport getReportById(Long id) {
        PostReport report = postReportMapper.selectById(id);
        if (report == null) {
            throw new ResourceNotFoundException("Report not found with id: " + id);
        }
        return report;
    }

    public void reviewReport(Long id, ReportReviewRequest request, Long id1) {
        PostReport report = postReportMapper.selectById(id);
        if (report == null) {
            throw new ResourceNotFoundException("Report not found with id: " + id);
        }

        try {
            report.setStatus(request.getStatus());
            report.setReviewTime(LocalDateTime.now());
            report.setAdminId(id1);
            postReportMapper.updateById(report);

            // 发送通知给举报人
            User reporter = userMapper.selectById(report.getReporterId());
            if (reporter != null) {
                notificationService.sendNotification(
                        reporter,
                        "您的举报已被处理，状态：" + request.getStatus(),
                        "REPORT_REVIEW"
                );
            }
        } catch (Exception e) {
            log.error("审核举报失败: {}", e.getMessage());
            throw new BusinessException("审核操作失败: " + e.getMessage());
        }
    }


    public IPage<AuditLog> getAuditLogs(int page, int size) {
        IPage<AuditLog> mpPage = new Page<>(page, size);
        return auditLogMapper.selectPage(mpPage, null);
    }

    public IPage<User> getUsers(int page, int size) {
        return userMapper.selectPage(new Page<>(page, size), null);
    }

    public IPage<Post> getPendingPosts(int page, int size) {
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("audited", false);
        return postMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public IPage<PostReport> getReports(int page, int size, String status) {
        QueryWrapper<PostReport> wrapper = new QueryWrapper<>();
        if (status != null) wrapper.eq("status", status);
        return postReportMapper.selectPage(new Page<>(page, size), wrapper);
    }


}
