package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.RestaurantDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RestaurantMapper {

@Select("SELECT * FROM merchants WHERE name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')")
    IPage<RestaurantDTO> searchByKeyword(Page<RestaurantDTO> pageParam, String keyword);

}
