package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.tastysphere_api.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    // 1. 更新点赞数
    @Update("UPDATE posts SET like_count = like_count + #{delta} WHERE post_id = #{postId}")
    void updateLikeCount(@Param("postId") Long postId, @Param("delta") int delta);

    // 2. 更新评论数
    @Update("UPDATE posts SET comment_count = comment_count + #{delta} WHERE post_id = #{postId}")
    void updateCommentCount(@Param("postId") Long postId, @Param("delta") int delta);

    // 3. 获取用户可见的帖子（复杂条件建议写 XML，简化为 SQL）
    IPage<Post> findVisiblePosts(IPage<Post> page,
                                 @Param("userId") Long userId,
                                 @Param("friendIds") List<Long> friendIds);

    // 4. 最新审核通过的帖子
    List<Post> findTop10ByAuditedTrueAndApprovedTrueOrderByCreatedTimeDesc();

    // ✅ 5. 新增：统计某时间之后创建的帖子数量
    @Select("SELECT COUNT(*) FROM posts WHERE created_time >= #{since}")
    long countSince(@Param("since") LocalDateTime since);


}
