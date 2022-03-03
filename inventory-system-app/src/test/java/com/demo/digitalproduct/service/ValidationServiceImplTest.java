package com.demo.digitalproduct.service;

import com.demo.digitalproduct.client.response.ProductExternalInfoSuccessResponse;
import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.entity.Product;
import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.demo.digitalproduct.util.TestingUtil.getMappedObjectFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ValidationServiceImplTest.TestConfiguration.class)
class ValidationServiceImplTest {

    static class TestConfiguration {
        @Bean
        public ValidationService validationService(final Validator validator) {
            return new ValidationServiceImpl(validator);
        }
    }

    @Autowired
    private ValidationService validationService;

    @MockBean
    private Validator validator;

    private static ProductDto productDto;
    private static final String PRODUCT_DTO = "productDto.json";

    @BeforeEach
    void setUp() throws IOException {
        productDto = getMappedObjectFromFile(PRODUCT_DTO, ProductDto.class);
    }

    @Test
    void validateProductDto_NoViolations() {
        // Arrange
        Set<ConstraintViolation<ProductDto>> constraintViolationSet = new HashSet<>();

        when(validator.validate(productDto)).thenReturn(constraintViolationSet);

        // Act
        Set<String> violationsSet = validationService.validateProductDto(productDto);

        // Assert
        assertEquals(0, violationsSet.size());
    }
}