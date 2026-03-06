package com.restaurant.order_management.service.impl;

import com.restaurant.order_management.dto.ProductDto;
import com.restaurant.order_management.entity.Branch;
import com.restaurant.order_management.entity.Product;
import com.restaurant.order_management.exception.ResourceNotFoundException;
import com.restaurant.order_management.repository.BranchRepository;
import com.restaurant.order_management.repository.ProductRepository;
import com.restaurant.order_management.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Branch branch = branchRepository.findById(productDto.getBranchId())
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", productDto.getBranchId()));

        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .branch(branch)
                .build();

        return mapToDto(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return mapToDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAllByIsDeletedFalse()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByBranch(Long branchId) {
        return productRepository.findAllByBranchIdAndIsDeletedFalse(branchId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        if (productDto.getBranchId() != null &&
                !productDto.getBranchId().equals(product.getBranch().getId())) {
            Branch branch = branchRepository.findById(productDto.getBranchId())
                    .filter(b -> !b.isDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch", productDto.getBranchId()));
            product.setBranch(branch);
        }

        return mapToDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setDeleted(true);
        productRepository.save(product);
    }

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .branchId(product.getBranch() != null ? product.getBranch().getId() : null)
                .build();
    }
}
