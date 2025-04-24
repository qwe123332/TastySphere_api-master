package com.example.tastysphere_api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.entity.Permission;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.mapper.PermissionMapper;
import com.example.tastysphere_api.mapper.PostMapper;
import com.example.tastysphere_api.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.test.mockmvc.auto-configure=false" // 可避免加载 MockReset 监听器
})

public class MybatisPlusSampleTest {

    @Autowired private UserMapper userMapper;
    @Autowired private PostMapper postMapper;
    @Autowired private PermissionMapper permissionMapper;

    // ✅ 查询全部用户
    @Test
    void testUserMapper_selectAll() {
        System.out.println("查询全部用户");
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
        assertThat(users).isNotNull();

    }

    // ✅ 按用户名模糊查找
    @Test
    void testUserMapper_queryByUsername() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username", "admin");
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);
        assertThat(users).isNotEmpty();
    }

    // ✅ 按 ID 查询用户
    @Test
    void testUserMapper_queryById() {
        Long userId = 1L;
        User user = userMapper.selectById(userId);
        System.out.println(user);
        assertThat(user).isNotNull();
    }


    // ✅ 插入一个新用户（用于开发测试，不推荐在生产测试类执行）
//    @Test
//    void testUserMapper_insert() {
//        User user = new User();
//        user.setUsername("test_user_" + System.currentTimeMillis());
//        user.setPassword("password123");
//        user.setEmail("test@example.com");
//        user.setPhoneNumber("13800000000");
//        user.setAvatar("avatar.jpg");
//        user.setCreatedTime(LocalDateTime.now());
//        user.setUpdatedTime(LocalDateTime.now());
//        user.setActive(true);
//        int rows = userMapper.insert(user);
//        System.out.println("Inserted user id: " + user.getId());
//        assertThat(rows).isEqualTo(1);
//    }

    // ✅ 查询所有帖子
    @Test
    void testPostMapper_selectAll() {
        List<Post> posts = postMapper.selectList(null);
        posts.forEach(System.out::println);
        assertThat(posts).isNotNull();
    }

    // ✅ 查询通过审核的帖子
    @Test
    void testPostMapper_auditedApproved() {
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("audited", true).eq("approved", true);
        List<Post> posts = postMapper.selectList(wrapper);
        posts.forEach(System.out::println);
        assertThat(posts).isNotEmpty();
    }

    // ✅ 按用户 ID 查询帖子
    @Test
    void testPostMapper_byUserId() {
        Long userId = 1L;
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Post> posts = postMapper.selectList(wrapper);
        posts.forEach(System.out::println);
        assertThat(posts).allMatch(p -> p.getUserId().equals(userId));
    }

    // ✅ 查询权限列表
    @Test
    void testPermissionMapper_selectAll() {
        List<Permission> permissions = permissionMapper.selectList(null);
        permissions.forEach(System.out::println);
        assertThat(permissions).isNotEmpty();
    }

    // ✅ 查询某权限是否存在
    @Test
    void testPermissionMapper_existsByName() {
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "ADMIN");
        List<Permission> list = permissionMapper.selectList(wrapper);
        assertThat(list).isNotEmpty();
    }
    //查询评论
    @Test
    void testPostMapper_selectComment() {
        Long postId = 2L;
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId);
        List<Post> posts = postMapper.selectList(wrapper);
        posts.forEach(System.out::println);
        assertThat(posts).isNotEmpty();
    }
    //getPostStats
    @Test
    void testPostMapper_getPostStats() {
        Long postId = 2L;
        Post post = postMapper.selectById(postId);
        assertThat(post).isNotNull();
        System.out.println("Post ID: " + post.getId());
        System.out.println("Like Count: " + post.getLikeCount());
        System.out.println("Comment Count: " + post.getCommentCount());
    }
}
