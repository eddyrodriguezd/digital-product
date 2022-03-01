package com.demo.digitalproduct.client;

import com.demo.digitalproduct.client.response.ProductExternalInfoSuccessResponse;
import org.springframework.http.ResponseEntity;

public interface ProductExternalInfoClient {

    ResponseEntity<ProductExternalInfoSuccessResponse> getProductExternalInfo(String productId);
}
