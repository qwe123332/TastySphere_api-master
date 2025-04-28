package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

/**
 * PostTag 实体类
 * 用于表示文章和标签之间的多对多关系
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("post_tag")

public class PostTag {
    private Long postId; // 替代 @ManyToOne (Post)
    private Long tagId; // 替代 @ManyToOne (Tag)

    // 其他字段和方法
}
