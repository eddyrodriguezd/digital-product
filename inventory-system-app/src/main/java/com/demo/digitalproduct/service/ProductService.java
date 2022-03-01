package com.demo.digitalproduct.service;

import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);

    ProductDto getProductById(String id);

    ProductDto updateProduct(String id, ProductDto productDto);
}
