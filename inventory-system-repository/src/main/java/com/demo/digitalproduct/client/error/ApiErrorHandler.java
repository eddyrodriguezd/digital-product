package com.demo.digitalproduct.client.error;

import com.demo.digitalproduct.client.exception.GenericProductInfoApiException;
import com.demo.digitalproduct.client.exception.ProductNotFoundInApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ApiErrorHandler {

    public static class ProductExternalInfoErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return !clientHttpResponse.getStatusCode().equals(HttpStatus.OK);
        }

        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
            if(clientHttpResponse.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new ProductNotFoundInApiException();
            throw new GenericProductInfoApiException();
        }
    }
}
