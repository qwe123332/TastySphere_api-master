package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.dto.PostDraftDTO;
import com.example.tastysphere_api.entity.PostDraft;
import com.example.tastysphere_api.mapper.PostDraftMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostDraftService {

    @Autowired
    private PostDraftMapper draftMapper;


    public PostDraftDTO saveDraft(PostDraftDTO dto, Long userId) {
        PostDraft draft = new PostDraft();
        BeanUtils.copyProperties(dto, draft);
        draft.setUserId(userId);
        draft.setLastSaved(LocalDateTime.now());
        if (dto.getId() == null) {
            draftMapper.insert(draft);
        } else {
            draft.setId(dto.getId());
            draftMapper.updateById(draft);
        }
        dto.setId(draft.getId());
        return dto;
    }


    public List<PostDraftDTO> getMyDrafts(Long userId) {
        return draftMapper.selectList(new QueryWrapper<PostDraft>().eq("user_id", userId))
                .stream()
                .map(draft -> {
                    PostDraftDTO dto = new PostDraftDTO();
                    BeanUtils.copyProperties(draft, dto);
                    return dto;
                }).collect(Collectors.toList());
    }


    public void deleteDraft(Long draftId, Long userId) {
        draftMapper.delete(new QueryWrapper<PostDraft>()
                .eq("id", draftId).eq("user_id", userId));
    }
}
