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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.demo.digitalproduct.helper.UuidHelper.getStringFromUUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductExternalInfoClient productClient;

    private final CacheService cacheService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductExternalInfoClient productClient,
                              CacheService cacheService) {
        this.productRepository = productRepository;
        this.productClient = productClient;
        this.cacheService = cacheService;
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
        log.info("Product <{}> saved in database", StringUtils.normalizeSpace(product.toString()));

        productDto.setId(product.getId().toString());
        return productDto;
    }

    @Override
    public ProductDto getProductById(String productId) {
        Product product = this.findById(productId);

        ProductExternalInfoSuccessResponse productResponse;
        try {
            productResponse = productClient.getProductExternalInfo(productId).getBody();

            if(productResponse == null) throw new ProductNotFoundInApiException();

            log.info("Product info retrieved from Price and Stock Information Server = <{}>",
                    StringUtils.normalizeSpace(productResponse.toString()));
        }
        catch (ProductNotFoundInApiException e) {
            productResponse = ProductExternalInfoSuccessResponse.builder()
                    .id(productId)
                    .price(0)
                    .stock(0)
                    .build();
            log.info("Price and Stock Information Server does not have information about product with id = <{}>. " +
                    "Therefore, these attributes will be set to 0.", productId);
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
        if(!this.existsById(productId)) throw new ProductNotFoundInDatabaseException();

        Product product = productRepository.save(
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
        log.info("Product <{}> updated in database", product);

        return productDto;
    }

    private Product findById(String productId) throws ProductNotFoundInDatabaseException {
        Product productFromCache = cacheService.getFromCache(productId);

        if(productFromCache != null) return productFromCache;

        Product product = productRepository.findById(getStringFromUUID(productId))
                .orElseThrow(ProductNotFoundInDatabaseException::new);

        log.info("Product with id = <{}> found in database: <{}>",
                productId,
                StringUtils.normalizeSpace(product.toString()));

        cacheService.saveToCache(product);

        return product;
    }

    private boolean existsById(String productId) {
        Product productFromCache = cacheService.getFromCache(productId);

        if(productFromCache != null) return true;

        return productRepository.existsById(getStringFromUUID(productId));
    }
}
