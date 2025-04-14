package com.example.tastysphere_api.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostSearchHandler {

    private final PostMapper postMapper;

    public IPage<PostDTO> search(String keyword, int page, int pageSize) {
        Page<PostDTO> pageParam = new Page<>(page, pageSize);
        return postMapper.searchByKeyword(pageParam, keyword);
    }

}
