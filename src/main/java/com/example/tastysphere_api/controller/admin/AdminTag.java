package com.example.tastysphere_api.controller.admin;

import com.example.tastysphere_api.entity.Tag;
import com.example.tastysphere_api.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
@PreAuthorize("hasRole('ADMIN')") // 整个类只允许管理员访问
public class AdminTag {
    @Autowired
    private TagService tagService;
    @GetMapping("/tags")
public List<Tag> getTags() {
        return tagService.listTags();

    }
    @GetMapping("tags/{id}")
    public Tag getTagById(@PathVariable Long id) {
        return tagService.getTagById(id);
    }
}
