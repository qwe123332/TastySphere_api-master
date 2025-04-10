package com.example.tastysphere_api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.entity.Permission;
import com.example.tastysphere_api.mapper.PermissionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class TastySphereApiApplicationTests {

    @Autowired
    private PermissionMapper permissionMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testFindByName() {
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "USER_READ");

        Optional<Permission> permission = Optional.ofNullable(permissionMapper.selectOne(wrapper));
        assert permission.isPresent() : "USER_READ 权限未找到！";
    }

    @Test
    void deleteByName() {
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "USER_READ");

        permissionMapper.delete(wrapper);

        var permission = Optional.ofNullable(permissionMapper.selectOne(wrapper));
        assert permission.isEmpty() : "USER_READ 权限未删除！";
    }
}
