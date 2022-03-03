package com.demo.digitalproduct.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class TestingUtil {
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T getMappedObjectFromFile(String path, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(new ClassPathResource(path).getFile(), clazz);
    }
}
