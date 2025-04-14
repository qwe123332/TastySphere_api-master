package com.example.tastysphere_api.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSearchHandler {
     private final UserMapper userMapper;

    public IPage<UserDTO> search(String keyword, int page, int pageSize) {
        Page<UserDTO> pageParam = new Page<>(page, pageSize);
        return userMapper.searchByKeyword(pageParam, keyword);
    }


    // Add your search logic here
}
