package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.tastysphere_api.dto.TagCreateDTO;
import com.example.tastysphere_api.dto.TagUpdateDTO;
import com.example.tastysphere_api.entity.Tag;
import com.example.tastysphere_api.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;


    public void createTag(TagCreateDTO tagCreateDTO) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagCreateDTO, tag);
        tagMapper.insert(tag);
    }


    public void updateTag(TagUpdateDTO tagUpdateDTO) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagUpdateDTO, tag);
        tagMapper.updateById(tag);
    }


    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }


    public List<Tag> listTags() {
        return tagMapper.selectList(new QueryWrapper<>());
    }

    public Tag getTagById(Long id) {
        return tagMapper.selectById(id);
    }
}
