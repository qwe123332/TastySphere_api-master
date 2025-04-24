package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    // ✅ 自定义方法：根据角色名查询
    @Select("SELECT * FROM roles WHERE name = #{name} LIMIT 1")
    Role findByName(@Param("name") String name);

    @Select("SELECT r.* FROM roles r " +
            "JOIN user_roles ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{id}")
    List<Role> findRolesByUserId(Long id);
}
