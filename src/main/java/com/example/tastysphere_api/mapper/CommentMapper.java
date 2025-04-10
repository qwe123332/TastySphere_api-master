package com.example.tastysphere_api.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Update("UPDATE comments SET like_count = like_count + #{delta} WHERE id = #{commentId}")
    void updateLikeCount(@Param("commentId") Long commentId, @Param("delta") int delta);

    // 多 ID 批量查子评论
    List<Comment> findByParentCommentIdIn(@Param("parentIds") Collection<Long> parentIds);

    // 如果你需要通过 XML 写更复杂查询，也可以写在 CommentMapper.xml 中
}
