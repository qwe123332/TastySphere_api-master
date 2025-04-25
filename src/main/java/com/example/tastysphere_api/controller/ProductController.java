package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.entity.Product;
import com.example.tastysphere_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    // 1. 获取当前商家的商品
    @GetMapping
    public List<Product> getMyProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long merchantId = userDetails.getUser().getId();


        List<Product> productsByMerchantId = productService.getProductsByMerchantId(merchantId);
        return productsByMerchantId;
    }

    // 2. 创建新商品
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product product,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        product.setUserId(userDetails.getUser().getId());
        productService.createProduct(product);
        return ResponseEntity.ok("商品上架成功");
    }

    // 3. 修改商品
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id,
                                                @RequestBody Product product,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        product.setProductId(id);
        product.setUserId(userDetails.getUser().getId());
        productService.updateProduct(product);
        return ResponseEntity.ok("商品更新成功");
    }

    // 4. 删除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        productService.deleteProduct(id, userDetails.getUser().getId());
        return ResponseEntity.ok("商品已删除");
    }
    //根据商品ID获取商品详情
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

}
