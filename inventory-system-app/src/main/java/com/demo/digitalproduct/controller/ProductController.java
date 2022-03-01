package com.demo.digitalproduct.controller;

import com.demo.digitalproduct.dto.ProductDto;
import com.demo.digitalproduct.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        log.info("Create Product API endpoint called with body = <{}>", StringUtils.normalizeSpace(productDto.toString()));

        ProductDto product = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") String productId) {
        log.info("Get Product by ID API endpoint called with id = <{}>", productId);

        ProductDto product = productService.getProductById(productId);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") String productId,
                                                    @RequestBody ProductDto productDto) {
        log.info("Update Product API endpoint called with body = <{}> and id = <{}>",
                StringUtils.normalizeSpace(productDto.toString()),
                productId);

        ProductDto product = productService.updateProduct(productId, productDto);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }
}
