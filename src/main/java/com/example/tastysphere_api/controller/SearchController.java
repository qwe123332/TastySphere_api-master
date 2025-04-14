package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.response.CommonResponse;
import com.example.tastysphere_api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Autowired private PostService postService;
    @Autowired private RecommendationService recommendationService;
    @Autowired private PostReportService postReportService;
    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private PostDraftService postDraftService;

    @GetMapping("/search")
    public CommonResponse search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        switch (type) {
            case "posts":
                return  postService.searchPosts(keyword, page, pageSize);
            case "restaurants":
                return restaurantService.searchRestaurants(keyword, page, pageSize);
            case "users":
                return userService.searchUsers(keyword, page, pageSize);
            case "all":
            default:
                return mixedSearch(keyword, page, pageSize);
        }
    }


    /**
     * 多类型混合搜索
     */
    private CommonResponse mixedSearch(String keyword, int page, int pageSize) {
        CommonResponse postResponse = postService.searchPosts(keyword, page, pageSize);
        CommonResponse restaurantResponse = restaurantService.searchRestaurants(keyword, page, pageSize);
        CommonResponse userResponse = userService.searchUsers(keyword, page, pageSize);

        Map<String, Object> posts = Map.of(
                "items", postResponse.getData() != null ? postResponse.getData() : List.of(),
                "total", postResponse.getTotal() != null ? postResponse.getTotal() : 0
        );

        Map<String, Object> restaurants = Map.of(
                "items", restaurantResponse.getData() != null ? restaurantResponse.getData() : List.of(),
                "total", restaurantResponse.getTotal() != null ? restaurantResponse.getTotal() : 0
        );

        Map<String, Object> users = Map.of(
                "items", userResponse.getData() != null ? userResponse.getData() : List.of(),
                "total", userResponse.getTotal() != null ? userResponse.getTotal() : 0,
                "test", "test"
        );
        Map<String, Object> data = new HashMap<>();
        data.put("posts", posts);
        data.put("restaurants", restaurants);
        data.put("users", users);
        return new CommonResponse(200, "搜索结果2", data);

    }
}
