package com.restaurant.order_management.service;

import com.restaurant.order_management.dto.BranchDto;

import java.util.List;

public interface BranchService {

    BranchDto createBranch(BranchDto branchDto);

    BranchDto getBranchById(Long id);

    List<BranchDto> getAllBranches();

    BranchDto updateBranch(Long id, BranchDto branchDto);

    void deleteBranch(Long id);
}
