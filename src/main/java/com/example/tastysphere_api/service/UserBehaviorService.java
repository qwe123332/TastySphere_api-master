package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.entity.UserBehavior;
import com.example.tastysphere_api.mapper.UserBehaviorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBehaviorService {

    @Autowired
    private UserBehaviorMapper behaviorMapper;

    public void recordBehavior(User user, String behaviorType,
                               Long targetId, String targetType, Double weight) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(user.getId()); // ✅ 设置 userId，而不是 user
        behavior.setBehaviorType(behaviorType);
        behavior.setTargetId(targetId);
        behavior.setTargetType(targetType);
        behavior.setWeight(weight);
        behaviorMapper.insert(behavior);
    }

    public List<UserBehavior> getUserBehaviors(Long userId, String behaviorType) {
        QueryWrapper<UserBehavior> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("behavior_type", behaviorType);
        return behaviorMapper.selectList(wrapper);
    }
}
