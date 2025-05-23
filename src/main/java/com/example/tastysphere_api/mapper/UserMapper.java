package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 基础 CRUD 已由 BaseMapper 提供
    @Select("SELECT user_id, username, password, email, phone_number, avatar, created_time, updated_time, status, is_active AS active FROM users")
    List<User> selectAllUsers();

    long countSince(LocalDateTime time);


    @Select("SELECT * FROM users WHERE username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%')")
    IPage<UserDTO> searchByKeyword(Page<UserDTO> pageParam, String keyword);

}
