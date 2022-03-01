package com.demo.digitalproduct.service;

import com.demo.digitalproduct.dto.ProductDto;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);

    ProductDto getProductById(String productId);

    ProductDto updateProduct(String productId, ProductDto productDto);
}
