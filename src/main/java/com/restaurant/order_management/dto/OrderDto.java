package com.restaurant.order_management.dto;

import com.restaurant.order_management.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private String customerName;
    private String customerEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Long branchId;
    private List<OrderItemDto> orderItems;
}
