package com.demo.digitalproduct.client;

import com.demo.digitalproduct.client.exception.GenericProductInfoApiException;
import com.demo.digitalproduct.client.exception.ProductNotFoundInApiException;
import com.demo.digitalproduct.client.response.ProductExternalInfoSuccessResponse;
import com.demo.digitalproduct.config.AppConfig;
import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.demo.digitalproduct.util.TestingUtil.getMappedObjectFromFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProductExternalInfoClientImplTest.TestConfiguration.class)
class ProductExternalInfoClientImplTest {

    static class TestConfiguration {
        @Bean
        public ProductExternalInfoClient productClient(final AppConfig appConfig) {
            return new ProductExternalInfoClientImpl(appConfig);
        }

        @Bean
        public AppConfig appConfig() {
            return AppConfig.builder()
                    .productExternalInfoEndpoint("http://localhost:1080/api/inventory/products/{id}")
                    .build();
        }
    }

    @Autowired
    private ProductExternalInfoClient productClient;

    @Autowired
    private ProductExternalInfoClient appConfig;

    private static ClientAndServer mockServer;

    private static ProductExternalInfoSuccessResponse productExternalInfo;
    private static final String PRODUCT_EXTERNAL_INFO = "productExternalInfo.json";

    @BeforeEach
    void setUp() throws IOException {
        productExternalInfo = getMappedObjectFromFile(PRODUCT_EXTERNAL_INFO, ProductExternalInfoSuccessResponse.class);

        mockServer = ClientAndServer.startClientAndServer(1080);
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    void getProductExternalInfo_Ok_200Code() {
        // Arrange
        String randomProductUUIDStr = UUID.randomUUID().toString();
        productExternalInfo.setId(randomProductUUIDStr);

        mockServer.when(request()
                .withPath("/api/inventory/products/" + randomProductUUIDStr)
                .withMethod("GET")
        ).respond(response()
                .withStatusCode(200)
                .withContentType(MediaType.APPLICATION_JSON_UTF_8)
                .withBody(StringUtils.normalizeSpace(productExternalInfo.toString())));

        // Act
        ProductExternalInfoSuccessResponse response =
                productClient.getProductExternalInfo(randomProductUUIDStr).getBody();

        // Assert
        assertThat(response)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", randomProductUUIDStr)
                .hasFieldOrPropertyWithValue("price", 899.99)
                .hasFieldOrPropertyWithValue("stock", 10.0);
    }

    @Test
    void getProductExternalInfo_ProductNotFoundInApiException_404Code() {
        // Arrange
        String randomProductUUIDStr = UUID.randomUUID().toString();
        productExternalInfo.setId(randomProductUUIDStr);

        mockServer.when(request()
                .withPath("/api/inventory/products/" + randomProductUUIDStr)
                .withMethod("GET")
        ).respond(response()
                .withStatusCode(404)
                .withContentType(MediaType.APPLICATION_JSON_UTF_8));

        // Act & Assert
        assertThrows(ProductNotFoundInApiException.class,
                () -> productClient.getProductExternalInfo(randomProductUUIDStr).getBody());
    }

    @Test
    void getProductExternalInfo_GenericProductInfoApiException_500Code() {
        // Arrange
        String randomProductUUIDStr = UUID.randomUUID().toString();
        productExternalInfo.setId(randomProductUUIDStr);

        mockServer.when(request()
                .withPath("/api/inventory/products/" + randomProductUUIDStr)
                .withMethod("GET")
        ).respond(response()
                .withStatusCode(500)
                .withContentType(MediaType.APPLICATION_JSON_UTF_8));

        // Act & Assert
        assertThrows(GenericProductInfoApiException.class,
                () -> productClient.getProductExternalInfo(randomProductUUIDStr).getBody());
    }
}