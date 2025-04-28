package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.PostDTO;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.dto.mapper.PostDtoMapper;
import com.example.tastysphere_api.dto.response.CommonResponse;
import com.example.tastysphere_api.entity.PostTag;
import com.example.tastysphere_api.entity.Tag;
import com.example.tastysphere_api.enums.VisibilityEnum;
import com.example.tastysphere_api.mapper.PostMapper;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.exception.ResourceNotFoundException;
import com.example.tastysphere_api.mapper.PostTagMapper;
import com.example.tastysphere_api.mapper.TagMapper;
import com.example.tastysphere_api.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.Utilities;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @Autowired
    private PostDtoMapper postDtoMapper;

    private final UserService userService;
    @Autowired
    private  PostTagMapper postTagMapper;
@Autowired
    private TagMapper tagMapper;
    @Autowired
    public PostService(@Lazy UserService userService) {  // 添加 @Lazy 注解
        this.userService = userService;
    }
    public PostDTO getPost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        if (!post.getApproved() && post.getAudited()) {
            throw new ResourceNotFoundException("Post is not available");
        }

        return convertToDTO(post);
    }




// 注入用户服务

    private PostDTO convertToDTO(Post post) {
        UserDTO user = userService.getUserById(post.getUserId()); // 查询用户
        return postDtoMapper.toDTOWithUserAndContext(post, user, null, null);
    }


    public List<PostDTO> getUserPosts(Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId);
        List<Post> posts = postMapper.selectList(wrapper);
        return posts.stream().map(this::convertToDTO).toList();
    }

    public void deletePost(Long postId, User currentUser) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found");
        }

        if (!post.getUserId().equals(currentUser.getId()) &&
                currentUser.getRoles().stream().noneMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }

        postMapper.deleteById(postId);
    }







    public List<PostDTO> getRecommendedPosts(User user) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getAudited, true).eq(Post::getApproved, true).orderByDesc(Post::getCreatedTime);
        Page<Post> page = new Page<>(0, 10);
        List<Post> posts = postMapper.selectPage(page, wrapper).getRecords();
        return posts.stream().map(this::convertToDTO).toList();
    }

    public void likePost(Long postId, User user) {
        postMapper.updateLikeCount(postId, 1);
    }

    public void unlikePost(Long postId, User user) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new ResourceNotFoundException("Post not found");

        if (post.getLikeCount() > 0) {
            postMapper.updateLikeCount(postId, -1);
        }
    }

    public IPage<PostDTO> getPostsByVisibility(VisibilityEnum visibility, Pageable pageable) {
        Page<Post> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getVisibility, visibility.name());

        IPage<Post> page = postMapper.selectPage(mpPage, wrapper);
        return page.convert(this::convertToDTO);
    }

    public IPage<PostDTO> getPendingAuditPosts(Pageable pageable) {
        Page<Post> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getAudited, false);
        return postMapper.selectPage(mpPage, wrapper).convert(this::convertToDTO);
    }

    public void auditPost(Long postId, boolean approved) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new ResourceNotFoundException("Post not found");

        post.setAudited(true);
        post.setApproved(approved);
        post.setAuditTime(LocalDateTime.now());

        postMapper.updateById(post);
    }

    public Map<String, Integer> getPostStats(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new ResourceNotFoundException("Post not found");

        Map<String, Integer> stats = new HashMap<>();
        stats.put("likeCount", post.getLikeCount());
        stats.put("commentCount", post.getCommentCount());
        return stats;
    }

    public CommonResponse searchPosts(String keyword, int page, int pageSize) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Post::getContent, keyword)
                .or().like(Post::getTitle, keyword)
                .orderByDesc(Post::getCreatedTime);

        Page<Post> mpPage = new Page<>(page, pageSize);
        IPage<Post> result = postMapper.selectPage(mpPage, wrapper);

        List<PostDTO> postDTOs = result.getRecords().stream()
                .map(this::convertToDTO)
                .toList();

        // 返回统一结构
        Map<String, Object> data = new HashMap<>();
        data.put("items", postDTOs);
        data.put("total", result.getTotal());

        return new CommonResponse(200, "Posts found", data);
    }

    // ✅ 管理后台专用：分页获取“待审核”帖子
    public IPage<PostDTO> getPendingPosts(Page<Post> page) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getAudited, false);  // ✅ 安全可用
        IPage<PostDTO> convert = postMapper.selectPage(page, wrapper).convert(this::convertToDTO);
        return convert.convert(post -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setId(post.getId());
            postDTO.setUserId(post.getUserId());

            postDTO.setTitle(post.getTitle());
            postDTO.setImages(post.getImages());
            postDTO.setLikeCount(post.getLikeCount());
            postDTO.setCommentCount(post.getCommentCount());

            postDTO.setContent(post.getContent());
            postDTO.setVisibility(post.getVisibility());
            postDTO.setCreatedTime(post.getCreatedTime());
            postDTO.setUsername(userService.getUserById(post.getUserId()).getUsername());
            postDTO.setUserAvatar(userService.getUserById(post.getUserId()).getAvatar());
        return postDTO;
        });
        //作者信息

    }

    // ✅ 管理后台专用：审核帖子（通过/拒绝 + 原因 + 审核人）
    public void auditPost(Long postId, boolean approved, User admin, String reason) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("帖子不存在");
        }
        post.setAudited(true);
        post.setApproved(approved);
        post.setAuditTime(LocalDateTime.now());
        postMapper.updateById(post);
    }

    // ✅ 管理后台专用：删除任意帖子（管理员强制删除）
    public void adminDelete(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("帖子不存在");
        }
        postMapper.deleteById(postId);
    }

    public IPage<PostDTO> getPosts(int page, int size, Long tagId) {
        Page<Post> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (tagId != null) {
            queryWrapper.inSql(Post::getId,
                    "SELECT post_id FROM post_tag WHERE tag_id = " + tagId);
        }
        queryWrapper.orderByDesc(Post::getCreatedTime);

        IPage<Post> postPage = postMapper.selectPage(pageRequest, queryWrapper);
        return postPage.convert(this::convertToDTO);
    }


    public IPage<PostDTO> getPostsByUser(Long userId, int page, int size) {
        IPage<Post> mpPage = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, userId);
        IPage<Post> postPage = postMapper.selectPage(mpPage, wrapper);
        return postPage.convert(this::convertToDTO);
    }


    public IPage<PostDTO> getAllPosts(IPage<Post> objectPage, String search) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            wrapper.like(Post::getContent, search)
                    .or().like(Post::getTitle, search);
        }
        IPage<Post> postPage = postMapper.selectPage(objectPage, wrapper);
        return postPage.convert(this::convertToDTO);
    }


    // ... 其余原有方法不动（略） ...
// 1. 修改createPost方法
    @Transactional
    public PostDTO createPost(PostDTO postDTO, User user) {
        Post post = new Post();
        BeanUtils.copyProperties(postDTO, post);
        post.setUserId(user.getId());
        post.setImages(postDTO.getImages()); // 添加封面图
        postMapper.insert(post);

        // 处理标签 - 现在接收的是tagIds列表
        if (postDTO.getTagIds() != null && !postDTO.getTagIds().isEmpty()) {
            List<PostTag> postTags = postDTO.getTagIds().stream()
                    .map(tagId -> {
                        PostTag pt = new PostTag();
                        pt.setPostId(post.getId());
                        pt.setTagId(tagId);
                        return pt;
                    }).collect(Collectors.toList());
            postTagMapper.batchInsert(postTags);
        }

        // 返回包含完整信息的DTO
        return getPostById(post.getId());
    }

    // 2. 修改updatePost方法
    public PostDTO updatePost(Long postId, PostDTO postDTO, User currentUser) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found");
        }

        if (!post.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this post");
        }

        // 更新字段
        post.setTitle(postDTO.getTitle());
        post.setContent(sensitiveWordService.filterContent(postDTO.getContent()));
        post.setVisibility(postDTO.getVisibility());
        post.setImages(postDTO.getImages());
        post.setAudited(false);
        post.setApproved(false);

        // 更新标签关联
        updatePostTags(postId, postDTO.getTagIds());

        postMapper.updateById(post);
        return getPostById(postId);
    }

    // 3. 添加更新标签关联的辅助方法
    private void updatePostTags(Long postId, List<Long> tagIds) {
        // 删除原有标签关联
        postTagMapper.delete(new QueryWrapper<PostTag>().eq("post_id", postId));

        // 添加新标签关联
        if (tagIds != null && !tagIds.isEmpty()) {
            List<PostTag> postTags = tagIds.stream()
                    .map(tagId -> {
                        PostTag pt = new PostTag();
                        pt.setPostId(postId);
                        pt.setTagId(tagId);
                        return pt;
                    }).collect(Collectors.toList());
            postTagMapper.batchInsert(postTags);
        }
    }

    // 4. 修改getPostById方法
    public PostDTO getPostById(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO);

        // 查询并设置标签ID列表
        List<Long> tagIds = postTagMapper.selectList(
                        new QueryWrapper<PostTag>().eq("post_id", postId))
                .stream()
                .map(PostTag::getTagId)
                .collect(Collectors.toList());
        postDTO.setTagIds(tagIds);

        // 查询完整的标签信息(可选)
        if (!tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            List<PostDTO.TagDTO> tagDTOList = tags.stream().map(tag -> {
                PostDTO.TagDTO tagDTO = new PostDTO.TagDTO();
                tagDTO.setId(tag.getId());
                tagDTO.setName(tag.getName());
                return tagDTO;
            }).collect(Collectors.toList());
            postDTO.setTags(tagDTOList);
        }

        return postDTO;
    }


    public PostDTO getPosts(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        List<PostTag> postTags = postTagMapper.selectList(new QueryWrapper<PostTag>().eq("post_id", postId));
        List<Long> tagIds = postTags.stream().map(PostTag::getTagId).collect(Collectors.toList());
        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        List<PostDTO.TagDTO> tagDTOList = tags.stream().map(tag -> {
            PostDTO.TagDTO tagDTO = new PostDTO.TagDTO();
            tagDTO.setId(tag.getId());
            tagDTO.setName(tag.getName());
            return tagDTO;
        }).collect(Collectors.toList());
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO);
        postDTO.setTags(tagDTOList);
        postDTO.setTagIds(tagIds);
        postDTO.setLikeCount(post.getLikeCount());
        postDTO.setCommentCount(post.getCommentCount());
        postDTO.setCreatedTime(post.getCreatedTime());
        postDTO.setUpdatedTime(post.getUpdatedTime());
        postDTO.setVisibility(post.getVisibility());
        //图片
        List<String> images = post.getImages().stream().map(image -> {
            String s = UrlUtils.resolveImageUrl(image);
            return s;
        }).collect(Collectors.toList());
        if (images != null && !images.isEmpty()) {
            postDTO.setImages(images);
        }
        postDTO.setImages(images);

        // 查询用户信息
        UserDTO user = userService.getUserById(post.getUserId());
        postDTO.setUserId(user.getId());
        postDTO.setUsername(user.getUsername());
        postDTO.setUserAvatar(user.getAvatar());

        return postDTO;

    }
}
