package com.example.tastysphere_api.service;

import com.example.tastysphere_api.entity.Order;
import com.example.tastysphere_api.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.findByUserId(userId); // 使用 MyBatis-Plus 的 Mapper 查询
    }

    public Optional<Order> getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId); // 使用 MyBatis-Plus 的 selectById 方法
        return Optional.ofNullable(order); // 转换为 Optional
    }

    public Order createOrder(Order order) {
        orderMapper.insert(order); // 使用 MyBatis-Plus 的 insert 方法
        return order;
    }

    public void deleteOrder(Long orderId) {
        orderMapper.deleteById(orderId); // 使用 MyBatis-Plus 的 deleteById 方法
    }
}
