package com.demo.digitalproduct.exception;

import org.springframework.http.HttpStatus;

import java.util.Set;

public class IllegalProductDtoException extends ApiException {

    public IllegalProductDtoException(Set<String> violations) {
        super("004", HttpStatus.BAD_REQUEST, "Product sent as a parameter is invalid. " +
                "It has the following errors: " + violations);
    }
}
