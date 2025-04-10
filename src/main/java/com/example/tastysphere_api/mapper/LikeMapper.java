package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    // 判断是否已点赞
    boolean existsByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);

    // 删除点赞
    void deleteByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);

    // 获取某帖的点赞数
    long countByPostId(@Param("postId") Long postId);

    // 判断是否已点赞（通过 postId 和 userId）
    boolean existsByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
