package com.example.tastysphere_api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tastysphere_api.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
