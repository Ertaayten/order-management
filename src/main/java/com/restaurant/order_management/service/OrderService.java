package com.restaurant.order_management.service;

import com.restaurant.order_management.dto.OrderDto;
import com.restaurant.order_management.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);

    OrderDto getOrderById(Long id);

    List<OrderDto> getAllOrders();

    List<OrderDto> getOrdersByBranch(Long branchId);

    List<OrderDto> getOrdersByStatus(OrderStatus status);

    OrderDto updateOrder(Long id, OrderDto orderDto);

    OrderDto updateOrderStatus(Long id, OrderStatus status);

    void deleteOrder(Long id);
}
