package com.demo.digitalproduct.exception;

import org.springframework.http.HttpStatus;

public class IllegalUUID extends ApiException {

    public IllegalUUID() {
        super("003", HttpStatus.NOT_FOUND, "Invalid UUID");
    }
}
