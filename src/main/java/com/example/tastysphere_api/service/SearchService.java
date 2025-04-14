package com.example.tastysphere_api.service;

import com.example.tastysphere_api.handler.PostSearchHandler;
import com.example.tastysphere_api.handler.RestaurantSearchHandler;
import com.example.tastysphere_api.handler.UserSearchHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostSearchHandler postSearchHandler;
    private final UserSearchHandler userSearchHandler;
    private final RestaurantSearchHandler restaurantSearchHandler;
    public Map<String, Object> search(String keyword, String type, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        switch (type) {
            case "posts":
                result.put("items", postSearchHandler.search(keyword, page, pageSize));
                break;
            case "users":
                result.put("items", userSearchHandler.search(keyword, page, pageSize));
                break;
            case "restaurants":
                result.put("items", restaurantSearchHandler.search(keyword, page, pageSize));
                break;
            case "all":
            default:
                result.put("posts", postSearchHandler.search(keyword, page, pageSize));
                result.put("users", userSearchHandler.search(keyword, page, pageSize));
                result.put("restaurants", restaurantSearchHandler.search(keyword, page, pageSize));
                break;
        }
        return result;
    }
}
