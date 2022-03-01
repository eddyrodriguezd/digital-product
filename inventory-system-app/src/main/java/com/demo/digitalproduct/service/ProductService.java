package com.demo.digitalproduct.service;

import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;

public interface ProductService {

    Product createProduct(ProductDto productDto);
}
