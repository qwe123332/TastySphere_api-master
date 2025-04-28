package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.TagCreateDTO;
import com.example.tastysphere_api.dto.TagUpdateDTO;
import com.example.tastysphere_api.entity.Tag;
import com.example.tastysphere_api.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public void create(@RequestBody @Valid TagCreateDTO tagCreateDTO) {
        tagService.createTag(tagCreateDTO);
    }

    @PutMapping
    public void update(@RequestBody @Valid TagUpdateDTO tagUpdateDTO) {
        tagService.updateTag(tagUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tagService.deleteTag(id);
    }

    @GetMapping
    public List<Tag> list() {
        return tagService.listTags();
    }
}
