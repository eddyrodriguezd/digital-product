package com.demo.digitalproduct.controller;

import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.service.ProductService;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.UUID;

import static com.demo.digitalproduct.util.TestingUtil.getMappedObjectFromFile;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = ProductController.class)
@ContextConfiguration(classes = ProductControllerTest.TestConfiguration.class)
class ProductControllerTest {

    static class TestConfiguration {
        @Bean
        public ProductController productController(ProductService productService) {
            return new ProductController(productService);
        }

        @Bean
        public ControllerExceptionHandler handler() {
            return new ControllerExceptionHandler();
        }
    }

    @Autowired
    private ProductController productController;

    @Autowired
    private ControllerExceptionHandler handler;

    @MockBean
    private ProductService productService;

    private MockMvc mockMvc;

    private static ProductDto productDto;
    private static final String PRODUCT_DTO = "productDto.json";

    @BeforeEach
    void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.standaloneSetup(productController).setControllerAdvice(handler).build();

        productDto = getMappedObjectFromFile(PRODUCT_DTO, ProductDto.class);
    }

    @Test
    void createProduct() throws Exception {
        // Arrange
        String randomUUIDStr = UUID.randomUUID().toString();
        ProductDto productDtoWithUUID = SerializationUtils.clone(productDto);
        productDtoWithUUID.setId(randomUUIDStr);

        when(productService.createProduct(productDto)).thenReturn(productDtoWithUUID);

        // Act (perform)
        mockMvc.perform(post("/api/products/create")
                        .content(productDto.toString())
                        .contentType(MediaType.APPLICATION_JSON))

        //Assert (expect, verify)
                .andExpect(status().isOk())
                .andExpect(content().json(productDtoWithUUID.toString()));

        verify(productService, times(1)).createProduct(productDto);
    }

    @Test
    void getProductById() throws Exception {
        // Arrange
        String randomUUIDStr = UUID.randomUUID().toString();
        productDto.setId(randomUUIDStr);

        when(productService.getProductById(randomUUIDStr)).thenReturn(productDto);

        // Act (perform)
        mockMvc.perform(get("/api/products/get/" + randomUUIDStr))

        //Assert (expect, verify)
                .andExpect(status().isOk())
                .andExpect(content().json(productDto.toString()));

        verify(productService, times(1)).getProductById(randomUUIDStr);
    }

    @Test
    void updateProduct() throws Exception {
        // Arrange
        String randomUUIDStr = UUID.randomUUID().toString();
        productDto.setId(randomUUIDStr);

        ProductDto newProductDto = SerializationUtils.clone(productDto);
        newProductDto.setDescription("Dell Inspiron 13");

        when(productService.updateProduct(randomUUIDStr, newProductDto)).thenReturn(newProductDto);

        // Act (perform)
        mockMvc.perform(put("/api/products/update/" + randomUUIDStr)
                .content(newProductDto.toString())
                .contentType(MediaType.APPLICATION_JSON))

        //Assert (expect, verify)
                .andExpect(status().isOk())
                .andExpect(content().json(newProductDto.toString()));

        verify(productService, times(1)).updateProduct(randomUUIDStr, newProductDto);
    }
}