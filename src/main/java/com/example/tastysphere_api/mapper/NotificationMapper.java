package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    // 根据 userId 获取通知并分页排序
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, QueryWrapper<Notification> queryWrapper);

    // 统计未读通知的数量
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND is_read = false")
    long countByUserIdAndReadFalse(@Param("userId") Long userId);
}
