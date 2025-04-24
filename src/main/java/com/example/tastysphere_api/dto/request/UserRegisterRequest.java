package com.example.tastysphere_api.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.entity.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;
    //status
    @NotBlank//notblank 作用是什么

    private String status;
    @NotBlank
    //admin和user和
    private String role; // 新增字段


    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setStatus(status);

        //private List<Role> roles = new ArrayList<>();
        List<Role> roles = new ArrayList<>();
        if (role != null) {
            Role roleEntity = new Role();
            roleEntity.setName(role);
            roles.add(roleEntity);
        }
        return user;
    }
}