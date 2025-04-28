package com.example.tastysphere_api.service;
import com.example.tastysphere_api.entity.Product;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class MallService {

    public List<Product> getRecommendedProducts(int page, int size) {

        return List.of(); // 返回空列表作为占位符
    }
}
