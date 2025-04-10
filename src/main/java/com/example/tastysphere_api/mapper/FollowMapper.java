package com.example.tastysphere_api.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    // 判断是否已关注
    boolean existsByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    // 删除关注关系
    void deleteByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    // 获取用户的关注列表
    List<Follow> findByFollowerId(@Param("followerId") Long followerId);

    // 获取用户的粉丝列表
    List<Follow> findByFollowingId(@Param("followingId") Long followingId);

    // 获取用户的所有关注ID
    List<Long> findFollowingIdsByFollower(@Param("followerId") Long followerId);

    // 获取关注数量
    long countByFollower(@Param("followerId") Long followerId);

    // 获取粉丝数量
    long countByFollowing(@Param("followingId") Long followingId);
}
