package com.example.tastysphere_api.service;
import com.example.tastysphere_api.entity.PostReport;
import com.example.tastysphere_api.mapper.PostReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostReportService {

    @Autowired
    private PostReportMapper postReportMapper;
    public void reportContent(Long postId, Long commentId, Long reporterId, String reason) {
        if ((postId == null && commentId == null) || (postId != null && commentId != null)) {
            throw new IllegalArgumentException("必须指定帖子或评论中的一个");
        }

        PostReport report = new PostReport();
        report.setPostId(postId);
        report.setCommentId(commentId);
        report.setReporterId(reporterId);
        report.setReason(reason);
        report.setCreatedAt(LocalDateTime.now());

        postReportMapper.insert(report);
    }
}
