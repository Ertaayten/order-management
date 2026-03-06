package com.restaurant.order_management.service;

import com.restaurant.order_management.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);

    ProductDto getProductById(Long id);

    List<ProductDto> getAllProducts();

    List<ProductDto> getProductsByBranch(Long branchId);

    ProductDto updateProduct(Long id, ProductDto productDto);

    void deleteProduct(Long id);
}
