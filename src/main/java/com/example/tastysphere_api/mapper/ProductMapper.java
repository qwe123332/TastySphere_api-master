package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Select("SELECT * FROM products WHERE products.user_id = #{id}")
    List<Product> selectByMerchantId(Long id);


}
