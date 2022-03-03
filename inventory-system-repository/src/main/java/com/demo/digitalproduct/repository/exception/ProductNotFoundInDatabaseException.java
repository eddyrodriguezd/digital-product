package com.demo.digitalproduct.repository.exception;

import com.demo.digitalproduct.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundInDatabaseException extends ApiException {

    public ProductNotFoundInDatabaseException() {
        super("001", HttpStatus.NOT_FOUND, "Requested product couldn't be found in database");
    }
}
