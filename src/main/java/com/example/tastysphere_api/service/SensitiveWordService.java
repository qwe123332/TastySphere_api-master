package com.example.tastysphere_api.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.HashSet;

@Service
public class SensitiveWordService {
    private final Set<String> sensitiveWords;

    public SensitiveWordService() {
        sensitiveWords = new HashSet<>();
        // 添加敏感词
        sensitiveWords.add("spam");
        // 添加更多敏感词...
    }

    public String filterContent(String content) {
        String filteredContent = content;
        for (String word : sensitiveWords) {
            filteredContent = filteredContent.replaceAll("(?i)" + word, "***");
        }
        return filteredContent;
    }
} 