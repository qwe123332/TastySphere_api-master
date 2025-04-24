package com.example.tastysphere_api.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.tastysphere_api.entity.Customer;
import com.example.tastysphere_api.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public IPage<Customer> getCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return customerService.getCustomerPage(keyword, page, pageSize);
    }
}
