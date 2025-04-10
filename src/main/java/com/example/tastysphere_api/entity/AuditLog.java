package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("audit_logs")
public class AuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("admin_id")
    private Long adminId; // 存储用户 ID（去除@ManyToOne）

    @TableField("action_type")
    private String actionType; // POST_AUDIT, USER_BAN, etc.

    @TableField("target_id")
    private Long targetId;

    @TableField("action_detail")
    private String actionDetail;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 如果需要通过 User 映射 admin 额外字段，建议在查询时填充
    @TableField(exist = false)
    private String adminUsername; // 映射 admin 的用户名等信息

    public void setAdmin(User admin) {
        this.adminId = admin.getId(); // 直接设置 adminId
        this.adminUsername = admin.getUsername(); // 额外字段


    }
}
