package com.demo.digitalproduct.client;

import com.demo.digitalproduct.client.error.ApiErrorHandler;
import com.demo.digitalproduct.client.response.ProductExternalInfoSuccessResponse;
import com.demo.digitalproduct.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
@Slf4j
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

        log.info("Request to Price and Stock Information Server will be sent with the following parameters: " +
                "URL = <{}>. Headers = <{}>. Id = <{}>", url, headers, productId);

        ResponseEntity<ProductExternalInfoSuccessResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductExternalInfoSuccessResponse.class,
                productId);

        log.info("Response from Price and Stock Information Server with Id = <{}>: <{}>",
                productId,
                StringUtils.normalizeSpace(response.toString()));

        return response;
    }
}
