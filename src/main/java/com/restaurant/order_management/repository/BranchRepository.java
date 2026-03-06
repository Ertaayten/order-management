package com.restaurant.order_management.repository;

import com.restaurant.order_management.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findAllByIsDeletedFalse();
}
