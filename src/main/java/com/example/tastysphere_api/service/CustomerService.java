package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.entity.Customer;
import com.example.tastysphere_api.mapper.CustomerMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    public IPage<Customer> getCustomerPage(String keyword, int page, int pageSize) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Customer::getName, keyword)
                    .or().like(Customer::getPhone, keyword);
        }
        return customerMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }
}
