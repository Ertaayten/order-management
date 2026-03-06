package com.restaurant.order_management.repository;

import com.restaurant.order_management.entity.Order;
import com.restaurant.order_management.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBranchIdAndIsDeletedFalse(Long branchId);

    List<Order> findAllByIsDeletedFalse();

    List<Order> findAllByStatusAndIsDeletedFalse(OrderStatus status);
}
