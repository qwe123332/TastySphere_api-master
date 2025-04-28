package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.SystemNotificationQueryDTO;
import com.example.tastysphere_api.entity.SystemNotification;
import com.example.tastysphere_api.mapper.SystemNotificationMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemNotificationService {

    @Autowired
    private SystemNotificationMapper systemNotificationMapper;


    public boolean createNotification(SystemNotification notification) {
        return systemNotificationMapper.insert(notification) > 0;
    }


    public boolean deleteNotification(Long id) {
        return systemNotificationMapper.deleteById(id) > 0;
    }


    public SystemNotification getNotification(Long id) {
        return systemNotificationMapper.selectById(id);
    }


    public Page<SystemNotification> searchNotifications(SystemNotificationQueryDTO queryDTO) {
        QueryWrapper<SystemNotification> wrapper = new QueryWrapper<>();

        if (queryDTO.getType() != null) {
            wrapper.eq("type", queryDTO.getType());
        }
        if (queryDTO.getIsGlobal() != null) {
            wrapper.eq("is_global", queryDTO.getIsGlobal());
        }
        if (queryDTO.getIsRead() != null) {
            wrapper.eq("is_read", queryDTO.getIsRead());
        }
        if (queryDTO.getTitle() != null) {
            wrapper.like("title", queryDTO.getTitle());
        }
        if (queryDTO.getContent() != null) {
            wrapper.like("content", queryDTO.getContent());
        }
        if (queryDTO.getUserId() != null) {
            wrapper.eq("target_user_id", queryDTO.getUserId());
        }
        if (queryDTO.getStartDate() != null && queryDTO.getEndDate() != null) {
            wrapper.between("created_at", queryDTO.getStartDate(), queryDTO.getEndDate());
        }

        wrapper.orderByDesc("created_at");

        return systemNotificationMapper.selectPage(
                new Page<>(queryDTO.getPage(), queryDTO.getSize()),
                wrapper
        );
    }

    public boolean updateById(SystemNotification notification) {
        return systemNotificationMapper.updateById(notification) > 0;

    }
}
