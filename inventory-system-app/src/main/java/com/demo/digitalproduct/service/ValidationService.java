package com.demo.digitalproduct.service;

import com.demo.digitalproduct.dto.ProductDto;

import java.util.Set;

public interface ValidationService {

    Set<String> validateProductDto(ProductDto productDto);
}
