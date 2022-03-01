package com.demo.digitalproduct.service;

import com.demo.digitalproduct.dto.ProductDetailDto;
import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;
import com.demo.digitalproduct.entity.ProductDetail;
import com.demo.digitalproduct.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productRepository.save(
                Product.builder()
                        .sku(productDto.getSku())
                        .description(productDto.getDescription())
                        .detail(
                                ProductDetail.builder()
                                        .visible(true)
                                        .build()
                        )
                        .build()
        );

        productDto.setId(product.getId().toString());
        return productDto;
    }

    @Override
    public ProductDto getProductById(String id) {
        Product product = productRepository.findById(UUID.fromString(id))
                .orElseThrow(RuntimeException::new);

        return ProductDto.builder()
                .id(product.getId().toString())
                .sku(product.getSku())
                .description(product.getDescription())
                .detail(ProductDetailDto.builder()
                        .currentPrice(340.50)
                        .currentStock(500)
                        .visible(product.getDetail().isVisible())
                        .build())
                .build();
    }

    @Override
    public ProductDto updateProduct(String productId, ProductDto productDto) {
        boolean productExists = productRepository.existsById(UUID.fromString(productId));

        if(!productExists) {
            throw new RuntimeException();
        }

        productRepository.save(
                Product.builder()
                        .id(UUID.fromString(productId))
                        .sku(productDto.getSku())
                        .description(productDto.getDescription())
                        .detail(
                                ProductDetail.builder()
                                        .visible(true)
                                        .build()
                        )
                        .build()
        );

        return productDto;
    }
}
