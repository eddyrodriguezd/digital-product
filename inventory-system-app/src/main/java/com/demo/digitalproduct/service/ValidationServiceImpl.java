package com.demo.digitalproduct.service;

import com.demo.digitalproduct.dto.ProductDto;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ValidationServiceImpl implements ValidationService{

    private final Validator validator;

    public ValidationServiceImpl(Validator validator) {
        this.validator = validator;
    }

    public Set<String> validateProductDto(ProductDto productDto) {
        return validator.validate(productDto).stream()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .collect(Collectors.toSet());
    }
}
