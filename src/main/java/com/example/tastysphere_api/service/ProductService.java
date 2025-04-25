package com.example.tastysphere_api.service;

import com.example.tastysphere_api.entity.Product;
import com.example.tastysphere_api.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取所有商品
     * @param page 页码
     * @param size 每页大小
     * @return 商品列表
     */
    public List<Product> getAllProducts(int page, int size) {
        int offset = (page - 1) * size;
        return productMapper.selectAllProducts(offset, size);
    }

    public Product getProductById(Long productId) {
        return productMapper.selectById(productId);
    }

    public List<Product> getProductsByMerchantId(Long merchantId) {
        return productMapper.selectByMerchantId(merchantId);
    }

    public Product createProduct(Product product) {
        productMapper.insert(product);
        return product;
    }

    public void deleteProduct(Long productId, Long id) {
        Product product = productMapper.selectById(productId);
        if (product != null && product.getUserId().equals(id)) {
            productMapper.deleteById(productId);
        } else {
            throw new IllegalArgumentException("Product not found or not owned by the merchant");
        }
    }

    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }
}
