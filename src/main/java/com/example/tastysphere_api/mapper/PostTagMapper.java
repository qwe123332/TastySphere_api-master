package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.PostTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostTagMapper   extends BaseMapper<PostTag> {


    void batchInsert(@Param("list") List<PostTag> list);

    @Select("SELECT tag_id FROM post_tag WHERE post_id = #{postId}")
    List<Long> selectTagIdsByPostId(@Param("postId") Long postId);

    @Delete("DELETE FROM post_tag WHERE post_id = #{postId}")
    void deleteByPostId(@Param("postId") Long postId);
}
