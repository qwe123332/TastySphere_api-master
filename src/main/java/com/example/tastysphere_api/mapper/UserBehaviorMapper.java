package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {

    @Select("SELECT * FROM user_behaviors WHERE user_id = #{userId} AND behavior_type = #{behaviorType}")
    List<UserBehavior> findByUserIdAndBehaviorType(@Param("userId") Long userId,
                                                   @Param("behaviorType") String behaviorType);
}
