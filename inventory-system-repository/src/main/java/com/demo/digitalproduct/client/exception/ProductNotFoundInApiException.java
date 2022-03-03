package com.demo.digitalproduct.client.exception;

import com.demo.digitalproduct.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundInApiException extends ApiException {

    public ProductNotFoundInApiException() {
        super("005", HttpStatus.NOT_FOUND, "Price and Stock Information Server does not have information about request product");
    }
}
