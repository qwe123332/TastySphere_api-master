package com.example.tastysphere_api.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.tastysphere_api.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    // 可以添加自定义SQL方法
}