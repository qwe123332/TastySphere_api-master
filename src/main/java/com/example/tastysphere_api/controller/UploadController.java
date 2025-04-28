package com.example.remoteupload.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${upload.avatar-dir}")
    private String avatarDir;

    @Value("${upload.avatar-url-prefix}")
    private String avatarUrlPrefix;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("上传文件为空！");
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String filename = "avatar_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;

            // 保存路径
            File dest = new File(avatarDir, filename);

            // 如果目录不存在就创建
            dest.getParentFile().mkdirs();

            file.transferTo(dest);

            // 返回可以访问的URL
            Map<String, Object> result = new HashMap<>();
            result.put("url", avatarUrlPrefix + filename);
            result.put("message", "上传成功！");
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("上传失败：" + e.getMessage());
        }
    }
}
