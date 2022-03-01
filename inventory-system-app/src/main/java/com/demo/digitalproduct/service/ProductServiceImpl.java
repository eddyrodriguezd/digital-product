package com.demo.digitalproduct.service;

import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;
import com.demo.digitalproduct.entity.ProductDetail;
import com.demo.digitalproduct.repository.ProductRepository;

import java.util.UUID;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(ProductDto productDto) {
        return productRepository.save(
                Product.builder()
                        .id(UUID.randomUUID())
                        .sku(productDto.getSku())
                        .description(productDto.getDescription())
                        .detail(
                                ProductDetail.builder()
                                        .id(UUID.randomUUID())
                                        .visible(true)
                                        .build()
                        )
                        .build()
        );
    }
}
