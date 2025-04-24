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
    private ProductMapper productMapper;  // ✅ 修改接口名



    public Optional<Product> getProductById(Long productId) {
        return Optional.ofNullable(productMapper.selectById(productId));
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
