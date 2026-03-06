package com.restaurant.order_management.service.impl;

import com.restaurant.order_management.dto.AddressDto;
import com.restaurant.order_management.dto.BranchDto;
import com.restaurant.order_management.entity.Address;
import com.restaurant.order_management.entity.Branch;
import com.restaurant.order_management.exception.ResourceNotFoundException;
import com.restaurant.order_management.repository.BranchRepository;
import com.restaurant.order_management.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public BranchDto createBranch(BranchDto branchDto) {
        Branch branch = mapToEntity(branchDto);
        Branch savedBranch = branchRepository.save(branch);
        return mapToDto(savedBranch);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchDto getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));
        return mapToDto(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDto> getAllBranches() {
        return branchRepository.findAllByIsDeletedFalse()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public BranchDto updateBranch(Long id, BranchDto branchDto) {
        Branch branch = branchRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));

        branch.setName(branchDto.getName());
        branch.setPhone(branchDto.getPhone());
        branch.setEmail(branchDto.getEmail());

        if (branchDto.getAddress() != null) {
            if (branch.getAddress() == null) {
                branch.setAddress(new Address());
            }
            AddressDto addressDto = branchDto.getAddress();
            branch.getAddress().setStreet(addressDto.getStreet());
            branch.getAddress().setCity(addressDto.getCity());
            branch.getAddress().setState(addressDto.getState());
            branch.getAddress().setCountry(addressDto.getCountry());
            branch.getAddress().setZipCode(addressDto.getZipCode());
        }

        return mapToDto(branchRepository.save(branch));
    }

    @Override
    @Transactional
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));
        branch.setDeleted(true);
        branchRepository.save(branch);
    }

    private Branch mapToEntity(BranchDto dto) {
        Branch branch = Branch.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();

        if (dto.getAddress() != null) {
            AddressDto addressDto = dto.getAddress();
            Address address = Address.builder()
                    .street(addressDto.getStreet())
                    .city(addressDto.getCity())
                    .state(addressDto.getState())
                    .country(addressDto.getCountry())
                    .zipCode(addressDto.getZipCode())
                    .build();
            branch.setAddress(address);
        }
        return branch;
    }

    private BranchDto mapToDto(Branch branch) {
        AddressDto addressDto = null;
        if (branch.getAddress() != null) {
            Address address = branch.getAddress();
            addressDto = AddressDto.builder()
                    .id(address.getId())
                    .street(address.getStreet())
                    .city(address.getCity())
                    .state(address.getState())
                    .country(address.getCountry())
                    .zipCode(address.getZipCode())
                    .build();
        }

        return BranchDto.builder()
                .id(branch.getId())
                .name(branch.getName())
                .phone(branch.getPhone())
                .email(branch.getEmail())
                .address(addressDto)
                .build();
    }
}
