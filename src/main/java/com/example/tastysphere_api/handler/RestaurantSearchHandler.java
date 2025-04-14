package com.example.tastysphere_api.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.RestaurantDTO;
import com.example.tastysphere_api.mapper.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RestaurantSearchHandler {
     private final RestaurantMapper restaurantMapper;

    public IPage<RestaurantDTO> search(String keyword, int page, int pageSize) {
        Page<RestaurantDTO> pageParam = new Page<>(page, pageSize);
        return restaurantMapper.searchByKeyword(pageParam, keyword);
    }


    // Add your search logic here


}
