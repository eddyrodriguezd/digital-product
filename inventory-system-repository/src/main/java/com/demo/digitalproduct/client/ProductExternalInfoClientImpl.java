package com.demo.digitalproduct.client;

import com.demo.digitalproduct.client.error.ApiErrorHandler;
import com.demo.digitalproduct.client.response.ProductExternalInfoSuccessResponse;
import com.demo.digitalproduct.config.AppConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class ProductExternalInfoClientImpl implements ProductExternalInfoClient {

    private final AppConfig appConfig;

    public ProductExternalInfoClientImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public ResponseEntity<ProductExternalInfoSuccessResponse> getProductExternalInfo(String productId) {
        String url = appConfig.getProductExternalInfoEndpoint();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ApiErrorHandler.ProductExternalInfoErrorHandler());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ProductExternalInfoSuccessResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductExternalInfoSuccessResponse.class,
                productId);

        return response;
    }
}
