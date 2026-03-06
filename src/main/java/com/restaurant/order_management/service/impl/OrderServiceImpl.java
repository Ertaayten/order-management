package com.restaurant.order_management.service.impl;

import com.restaurant.order_management.dto.OrderDto;
import com.restaurant.order_management.dto.OrderItemDto;
import com.restaurant.order_management.entity.*;
import com.restaurant.order_management.exception.ResourceNotFoundException;
import com.restaurant.order_management.repository.BranchRepository;
import com.restaurant.order_management.repository.OrderRepository;
import com.restaurant.order_management.repository.ProductRepository;
import com.restaurant.order_management.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Branch branch = branchRepository.findById(orderDto.getBranchId())
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", orderDto.getBranchId()));

        Order order = Order.builder()
                .customerName(orderDto.getCustomerName())
                .customerEmail(orderDto.getCustomerEmail())
                .status(orderDto.getStatus() != null ? orderDto.getStatus() : OrderStatus.PENDING)
                .branch(branch)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        if (orderDto.getOrderItems() != null && !orderDto.getOrderItems().isEmpty()) {
            List<Long> productIds = orderDto.getOrderItems().stream()
                    .map(OrderItemDto::getProductId)
                    .toList();
            List<Product> products = productRepository.findAllById(productIds);
            Map<Long, Product> productMap = products.stream()
                    .filter(p -> !p.isDeleted())
                    .collect(Collectors.toMap(BaseEntity::getId, p -> p));

            for (OrderItemDto itemDto : orderDto.getOrderItems()) {
                Product product = productMap.get(itemDto.getProductId());
                if (product == null) {
                    throw new ResourceNotFoundException("Product", itemDto.getProductId());
                }
                OrderItem item = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(itemDto.getQuantity())
                        .unitPrice(product.getPrice())
                        .build();
                orderItems.add(item);
            }
        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotal(orderItems));

        return mapToDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return mapToDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAllByIsDeletedFalse()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByBranch(Long branchId) {
        return orderRepository.findAllByBranchIdAndIsDeletedFalse(branchId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findAllByStatusAndIsDeletedFalse(status)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        order.setCustomerName(orderDto.getCustomerName());
        order.setCustomerEmail(orderDto.getCustomerEmail());
        if (orderDto.getStatus() != null) {
            order.setStatus(orderDto.getStatus());
        }

        return mapToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setStatus(status);
        return mapToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setDeleted(true);
        orderRepository.save(order);
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItemDto mapItemToDto(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }

    private OrderDto mapToDto(Order order) {
        List<OrderItemDto> itemDtos = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream().map(this::mapItemToDto).toList();

        return OrderDto.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .branchId(order.getBranch().getId())
                .orderItems(itemDtos)
                .build();
    }
}
