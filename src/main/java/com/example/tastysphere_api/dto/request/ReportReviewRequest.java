package com.example.tastysphere_api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportReviewRequest {
    private Long reportId;
    private String status;
    private String reason;

}
