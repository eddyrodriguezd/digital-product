package com.demo.digitalproduct.service;

import com.demo.digitalproduct.client.ProductExternalInfoClient;
import com.demo.digitalproduct.client.response.ProductExternalInfoSuccessResponse;
import com.demo.digitalproduct.config.AppConfig;
import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;
import com.demo.digitalproduct.entity.ProductDetail;
import com.demo.digitalproduct.repository.ProductRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import static com.demo.digitalproduct.util.TestingUtil.getMappedObjectFromFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CacheServiceImplTest.TestConfiguration.class)
class CacheServiceImplTest {

    static class TestConfiguration {
        @Bean
        public CacheService cacheService(
                final RedisTemplate<String, Object> redisTemplate,
                final AppConfig appConfig) {
            return new CacheServiceImpl(redisTemplate, appConfig);
        }

        @Bean
        AppConfig appConfig() {
            return AppConfig.builder()
                    .productExternalInfoEndpoint("http://localhost:1080//api/inventory/products/{id}")
                    .cache(AppConfig.Cache.builder()
                            .host("localhost")
                            .port(6379)
                            .password("")
                            .duration(5)
                            .build())
                    .build();
        }
    }

    @Autowired
    private CacheService cacheService;
    @Autowired
    private AppConfig appConfig;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    @MockBean
    private ValueOperations<String, Object> valueOperations;

    private static Product productEntity;
    private static final String PRODUCT_ENTITY = "productEntity.json";

    @BeforeEach
    void setUp() throws IOException {
        productEntity = getMappedObjectFromFile(PRODUCT_ENTITY, Product.class);
    }

    @Test
    void getFromCache_Found() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        productEntity.setId(randomProductUUID);

        Product productFoundInCache = SerializationUtils.clone(productEntity);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(randomProductUUID.toString())).thenReturn(productFoundInCache);

        // Act
        Product productOutput = cacheService.getFromCache(randomProductUUID.toString());

        // Assert
        assertEquals(productOutput.getId(), productFoundInCache.getId());
    }

    @Test
    void getFromCache_NotFound() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        productEntity.setId(randomProductUUID);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(randomProductUUID.toString())).thenReturn(null);

        // Act
        Product productOutput = cacheService.getFromCache(randomProductUUID.toString());

        // Assert
        assertNull(productOutput);
    }

    @Test
    void saveToCache_Ok() {
        // Arrange
        UUID randomProductUUID = UUID.randomUUID();
        productEntity.setId(randomProductUUID);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(
                productEntity.getId().toString(),
                productEntity,
                Duration.ofMinutes(appConfig.getCache().getDuration())
        );

        // Act & Assert
        assertDoesNotThrow(() -> cacheService.saveToCache(productEntity));

    }
}