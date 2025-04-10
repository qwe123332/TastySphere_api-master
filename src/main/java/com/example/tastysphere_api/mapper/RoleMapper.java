package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    // ✅ 自定义方法：根据角色名查询
    @Select("SELECT * FROM roles WHERE name = #{name} LIMIT 1")
    Role findByName(@Param("name") String name);
}
