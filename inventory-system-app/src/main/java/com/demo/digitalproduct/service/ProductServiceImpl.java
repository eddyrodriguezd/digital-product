package com.demo.digitalproduct.service;

import com.demo.digitalproduct.client.ProductExternalInfoClient;
import com.demo.digitalproduct.client.exception.ProductNotFoundInApiException;
import com.demo.digitalproduct.client.response.ProductExternalInfoSuccessResponse;
import com.demo.digitalproduct.dto.ProductDetailDto;
import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;
import com.demo.digitalproduct.entity.ProductDetail;
import com.demo.digitalproduct.repository.ProductRepository;
import com.demo.digitalproduct.repository.exception.ProductNotFoundInDatabaseException;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.demo.digitalproduct.helper.UuidHelper.getStringFromUUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductExternalInfoClient productClient;

    public ProductServiceImpl(ProductRepository productRepository, ProductExternalInfoClient productClient) {
        this.productRepository = productRepository;
        this.productClient = productClient;
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
    public ProductDto getProductById(String productId) {
        Product product = productRepository.findById(getStringFromUUID(productId))
                .orElseThrow(ProductNotFoundInDatabaseException::new);

        ProductExternalInfoSuccessResponse productResponse;
        try {
            productResponse = (productClient.getProductExternalInfo(productId)).getBody();
        }
        catch (ProductNotFoundInApiException e) {
            productResponse = ProductExternalInfoSuccessResponse.builder()
                    .id(productId)
                    .price(0)
                    .stock(0)
                    .build();
        }

        return ProductDto.builder()
                .id(product.getId().toString())
                .sku(product.getSku())
                .description(product.getDescription())
                .detail(ProductDetailDto.builder()
                        .currentPrice(productResponse != null ? productResponse.getPrice() : 0)
                        .currentStock(productResponse != null ? productResponse.getStock() : 0)
                        .visible(product.getDetail().isVisible())
                        .build())
                .build();
    }

    @Override
    public ProductDto updateProduct(String productId, ProductDto productDto) {
        if(!productRepository.existsById(getStringFromUUID(productId)))
            throw new ProductNotFoundInDatabaseException();

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
