package com.demo.digitalproduct.controller;

import com.demo.digitalproduct.client.exception.GenericProductInfoApiException;
import com.demo.digitalproduct.exception.ApiException;
import com.demo.digitalproduct.exception.IllegalProductDtoException;
import com.demo.digitalproduct.exception.IllegalUUIDException;
import com.demo.digitalproduct.repository.exception.ProductNotFoundInDatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            ProductNotFoundInDatabaseException.class,
            GenericProductInfoApiException.class,
            IllegalUUIDException.class,
            IllegalProductDtoException.class
    })
    protected ResponseEntity<Object> handleInvalidURL(RuntimeException exception, WebRequest request) {
        ApiException ex = (ApiException) exception;
        log.error("Exception encountered: " + StringUtils.normalizeSpace(ex.getBody().toString()));
        return handleExceptionInternal(ex, ex.getBody(), new HttpHeaders(), ex.getBody().getStatus(), request);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleSQLConflict(RuntimeException exception, WebRequest request) {
        String body = "Request format is valid, but SQL statement could not be executed";
        return handleExceptionInternal(exception, body, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {RestClientException.class})
    protected ResponseEntity<Object> restClientException(RuntimeException exception, WebRequest request) {
        String body = "Connection refused when trying to send an HTTP request";
        return handleExceptionInternal(exception, body, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}