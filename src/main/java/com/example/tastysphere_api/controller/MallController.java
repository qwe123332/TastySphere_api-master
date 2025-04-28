package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.entity.Product;
import com.example.tastysphere_api.service.ProductService;
import com.example.tastysphere_api.service.MallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mall")
public class MallController {

     private final ProductService productService;
     private final MallService mallService;

    public MallController(ProductService productService, MallService mallService) {
        this.productService = productService;
        this.mallService = mallService;
    }

    /**
     * 获取所有商品
     * @param page 页码
     * @param size 每页大小
     * @return 商品列表
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    /**
     * 根据商品ID获取商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * 获取推荐商品
     * @param page 页码
     * @param size 每页大小
     * @return 推荐商品列表
     */
    @GetMapping("/products/recommended")
    public ResponseEntity<List<Product>> getRecommendedProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(mallService.getRecommendedProducts(page, size));
    }
}