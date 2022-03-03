package com.demo.digitalproduct.service;

import com.demo.digitalproduct.client.ProductExternalInfoClient;
import com.demo.digitalproduct.client.exception.ProductNotFoundInApiException;
import com.demo.digitalproduct.client.response.ProductExternalInfoSuccessResponse;
import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;
import com.demo.digitalproduct.entity.ProductDetail;
import com.demo.digitalproduct.exception.IllegalProductDtoException;
import com.demo.digitalproduct.repository.ProductRepository;
import com.demo.digitalproduct.repository.exception.ProductNotFoundInDatabaseException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.demo.digitalproduct.util.TestingUtil.getMappedObjectFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProductServiceImplTest.TestConfiguration.class)
class ProductServiceImplTest {

    static class TestConfiguration {
        @Bean
        public ProductService productService(
                final ProductRepository productRepository,
                final ProductExternalInfoClient productClient,
                final CacheService cacheService,
                final ValidationServiceImpl validationService) {
            return new ProductServiceImpl(productRepository, productClient, cacheService, validationService);
        }
    }

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductExternalInfoClient productClient;

    @MockBean
    private CacheService cacheService;

    @MockBean
    private ValidationServiceImpl validationService;

    private static ProductDto productDto;
    private static final String PRODUCT_DTO = "productDto.json";

    private static Product productEntity;
    private static final String PRODUCT_ENTITY = "productEntity.json";

    private static ProductExternalInfoSuccessResponse productExternalInfo;
    private static final String PRODUCT_EXTERNAL_INFO = "productExternalInfo.json";

    @BeforeEach
    void setUp() throws IOException {
        productDto = getMappedObjectFromFile(PRODUCT_DTO, ProductDto.class);
        productEntity = getMappedObjectFromFile(PRODUCT_ENTITY, Product.class);
        productExternalInfo = getMappedObjectFromFile(PRODUCT_EXTERNAL_INFO, ProductExternalInfoSuccessResponse.class);
    }

    @Test
    void createProduct_Ok() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        UUID randomProductDetailUUID = UUID.randomUUID();

        Product fullProductEntity = SerializationUtils.clone(productEntity);
        fullProductEntity.setId(randomProductUUID);

        ProductDetail fullProductDetailEntity = fullProductEntity.getDetail();
        fullProductDetailEntity.setId(randomProductDetailUUID);
        fullProductDetailEntity.setUpdatedAt(LocalDateTime.now());
        fullProductDetailEntity.setCreatedAt(LocalDateTime.now().minusDays(1));
        fullProductEntity.setDetail(fullProductDetailEntity);

        when(validationService.validateProductDto(productDto)).thenReturn(new HashSet<>());
        when(productRepository.save(productEntity)).thenReturn(fullProductEntity);

        // Act
        ProductDto productDtoOutput = productService.createProduct(productDto);

        // Assert
        assertEquals(randomProductUUID.toString(), productDtoOutput.getId());
    }

    @Test
    void createProduct_IllegalProductDtoException() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        UUID randomProductDetailUUID = UUID.randomUUID();

        Product fullProductEntity = SerializationUtils.clone(productEntity);
        fullProductEntity.setId(randomProductUUID);
        fullProductEntity.setDescription(null);

        ProductDetail fullProductDetailEntity = fullProductEntity.getDetail();
        fullProductDetailEntity.setId(randomProductDetailUUID);
        fullProductDetailEntity.setUpdatedAt(LocalDateTime.now());
        fullProductDetailEntity.setCreatedAt(LocalDateTime.now().minusDays(1));
        fullProductEntity.setDetail(fullProductDetailEntity);

        when(validationService.validateProductDto(productDto))
                .thenReturn(Set.of("description cannot be null"));

        // Act & Assert
        assertThrows(IllegalProductDtoException.class,
                () -> productService.createProduct(productDto));
    }

    @Test
    void getProductById_FindInDatabase_Ok() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        productDto.setId(randomProductUUID.toString());
        productEntity.setId(randomProductUUID);
        productExternalInfo.setId(randomProductUUID.toString());

        when(cacheService.getFromCache(randomProductUUID.toString())).thenReturn(null);
        when(productRepository.findById(randomProductUUID)).thenReturn(Optional.of(productEntity));
        doNothing().when(cacheService).saveToCache(productEntity);
        when(productClient.getProductExternalInfo(randomProductUUID.toString()))
                .thenReturn(new ResponseEntity<>(productExternalInfo, HttpStatus.OK));

        // Act
        ProductDto productDtoOutput = productService.getProductById(randomProductUUID.toString());

        // Assert
        assertEquals(randomProductUUID.toString(), productDtoOutput.getId());
        assertEquals(productExternalInfo.getPrice(), productDtoOutput.getDetail().getCurrentPrice());
        assertEquals(productExternalInfo.getStock(), productDtoOutput.getDetail().getCurrentStock());
        verify(productRepository, times(1)).findById(randomProductUUID);
    }

    @Test
    void getProductById_ProductNotFoundInDatabaseException() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        productDto.setId(randomProductUUID.toString());
        productEntity.setId(randomProductUUID);
        productExternalInfo.setId(randomProductUUID.toString());

        when(cacheService.getFromCache(randomProductUUID.toString())).thenReturn(null);
        when(productRepository.findById(randomProductUUID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundInDatabaseException.class,
                () -> productService.getProductById(randomProductUUID.toString()));
    }

    @Test
    void getProductById_ProductNotFoundInApiException() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        productDto.setId(randomProductUUID.toString());
        productEntity.setId(randomProductUUID);
        productExternalInfo.setId(randomProductUUID.toString());

        when(cacheService.getFromCache(randomProductUUID.toString())).thenReturn(null);
        when(productRepository.findById(randomProductUUID)).thenReturn(Optional.of(productEntity));
        doNothing().when(cacheService).saveToCache(productEntity);
        when(productClient.getProductExternalInfo(randomProductUUID.toString()))
                .thenThrow(ProductNotFoundInApiException.class);

        // Act
        ProductDto productDtoOutput = productService.getProductById(randomProductUUID.toString());

        // Assert
        assertEquals(randomProductUUID.toString(), productDtoOutput.getId());
        assertEquals(0, productDtoOutput.getDetail().getCurrentPrice());
        assertEquals(0, productDtoOutput.getDetail().getCurrentStock());
        verify(productRepository, times(1)).findById(randomProductUUID);
    }

    @Test
    void getProductById_FindInCache_Ok() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        productDto.setId(randomProductUUID.toString());
        productEntity.setId(randomProductUUID);
        productExternalInfo.setId(randomProductUUID.toString());

        when(cacheService.getFromCache(randomProductUUID.toString())).thenReturn(productEntity);
        when(productClient.getProductExternalInfo(randomProductUUID.toString()))
                .thenReturn(new ResponseEntity<>(productExternalInfo, HttpStatus.OK));

        // Act
        ProductDto productDtoOutput = productService.getProductById(randomProductUUID.toString());

        // Assert
        assertEquals(randomProductUUID.toString(), productDtoOutput.getId());
        assertEquals(productExternalInfo.getPrice(), productDtoOutput.getDetail().getCurrentPrice());
        assertEquals(productExternalInfo.getStock(), productDtoOutput.getDetail().getCurrentStock());
        verify(productRepository, times(0)).findById(randomProductUUID);
    }

    @Test
    void updateProduct_FindInDatabase_Ok() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        UUID randomProductDetailUUID = UUID.randomUUID();

        ProductDto newProductDto = SerializationUtils.clone(productDto);
        newProductDto.setDescription("Dell Inspiron 13");

        Product fullProductEntity = SerializationUtils.clone(productEntity);
        fullProductEntity.setId(randomProductUUID);

        ProductDetail fullProductDetailEntity = fullProductEntity.getDetail();
        fullProductDetailEntity.setId(randomProductDetailUUID);
        fullProductDetailEntity.setUpdatedAt(LocalDateTime.now());
        fullProductDetailEntity.setCreatedAt(LocalDateTime.now().minusDays(1));
        fullProductEntity.setDetail(fullProductDetailEntity);
        fullProductEntity.setDescription(newProductDto.getDescription());

        productDto.setId(randomProductUUID.toString());
        productEntity.setId(randomProductUUID);
        productExternalInfo.setId(randomProductUUID.toString());

        when(cacheService.getFromCache(randomProductUUID.toString())).thenReturn(null);
        when(productRepository.existsById(randomProductUUID)).thenReturn(true);
        when(productRepository.save(productEntity)).thenReturn(fullProductEntity);

        // Act
        ProductDto productDtoOutput = productService.updateProduct(randomProductUUID.toString(), newProductDto);

        // Assert
        assertEquals(newProductDto.getDescription(), productDtoOutput.getDescription());
        verify(productRepository, times(1)).existsById(randomProductUUID);
    }

    @Test
    void updateProduct_FindInCache_Ok() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        UUID randomProductDetailUUID = UUID.randomUUID();

        ProductDto newProductDto = SerializationUtils.clone(productDto);
        newProductDto.setDescription("Dell Inspiron 13");

        Product fullProductEntity = SerializationUtils.clone(productEntity);
        fullProductEntity.setId(randomProductUUID);

        ProductDetail fullProductDetailEntity = fullProductEntity.getDetail();
        fullProductDetailEntity.setId(randomProductDetailUUID);
        fullProductDetailEntity.setUpdatedAt(LocalDateTime.now());
        fullProductDetailEntity.setCreatedAt(LocalDateTime.now().minusDays(1));
        fullProductEntity.setDetail(fullProductDetailEntity);
        fullProductEntity.setDescription(newProductDto.getDescription());

        productDto.setId(randomProductUUID.toString());
        productEntity.setId(randomProductUUID);
        productExternalInfo.setId(randomProductUUID.toString());

        when(cacheService.getFromCache(randomProductUUID.toString())).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(fullProductEntity);

        // Act
        ProductDto productDtoOutput = productService.updateProduct(randomProductUUID.toString(), newProductDto);

        // Assert
        assertEquals(newProductDto.getDescription(), productDtoOutput.getDescription());
        verify(productRepository, times(0)).existsById(randomProductUUID);
    }
}