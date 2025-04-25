package com.example.tastysphere_api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.entity.Category;
import com.example.tastysphere_api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 创建分类
    @PostMapping
    public Category createCategory(@Validated @RequestBody Category category) {
        categoryService.save(category);
        return category;
    }

    // 获取所有分类
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.list();
    }

    // 分页查询分类
    @GetMapping("/page")
    public IPage<Category> getCategoriesByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return categoryService.page(new Page<>(pageNum, pageSize),
                new QueryWrapper<Category>().eq("deleted", 0));
    }

    // 获取单个分类
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    // 更新分类
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id,
                                   @Validated @RequestBody Category category) {
        category.setId(id);
        categoryService.updateById(category);
        return category;
    }

    // 删除分类（逻辑删除）
    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        return "删除成功";
    }
}