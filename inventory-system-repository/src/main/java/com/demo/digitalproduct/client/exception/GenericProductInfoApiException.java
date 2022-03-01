package com.demo.digitalproduct.client.exception;

import com.demo.digitalproduct.exception.ApiException;
import org.springframework.http.HttpStatus;

public class GenericProductInfoApiException extends ApiException {

    public GenericProductInfoApiException() {
        super("002", HttpStatus.CONFLICT, "Couldn't communicate to Price and Stock Information Server");
    }
}