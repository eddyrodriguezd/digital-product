package com.demo.digitalproduct.exception;

import org.springframework.http.HttpStatus;

public class IllegalUUIDException extends ApiException {

    public IllegalUUIDException() {
        super("003", HttpStatus.BAD_REQUEST, "UUID sent as a parameter is invalid");
    }
}
