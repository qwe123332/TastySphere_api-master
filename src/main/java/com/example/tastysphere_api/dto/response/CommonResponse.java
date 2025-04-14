package com.example.tastysphere_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CommonResponse {
    private int code;
    private String message;
    private Map<String, Object> data = new HashMap<>(); // 默认初始化
    private Long total; // 用于分页的总数

    // 构造函数确保data不为null
    public CommonResponse(int code, String message, Map<String, Object> data) {
        this.code = code;
        this.message = message;
        this.data = data != null ? data : new HashMap<>();
    }

    //    添加安全访问方法
//    public List<?> getItems() {
//        return (List<?>) data.getOrDefault("items", Collections.emptyList());
//    }
}