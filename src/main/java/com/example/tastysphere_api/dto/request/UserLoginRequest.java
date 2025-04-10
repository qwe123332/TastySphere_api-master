package com.example.tastysphere_api.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
