package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.entity.Category;
import com.example.tastysphere_api.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
private final CategoryMapper categoryMapper;
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }
    public void save(Category category) {
        categoryMapper.insert(category);

    }
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
    public void update(Category category) {
        categoryMapper.updateById(category);
    }
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }
    public List<Category> list() {
        return categoryMapper.selectList(null);
    }
    public List<Category> getCategoriesByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return categoryMapper.selectList(null)
                .subList(offset, Math.min(offset + pageSize, categoryMapper.selectList(null).size()));
    }

    public IPage<Category> page(Page<Category> objectPage, QueryWrapper<Category> deleted) {
        return categoryMapper.selectPage(objectPage, deleted);
    }

    public void updateById(Category category) {

        categoryMapper.updateById(category);

    }

    public void removeById(Long id) {
        categoryMapper.deleteById(id);
    }
}
