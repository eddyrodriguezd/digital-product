package com.demo.digitalproduct.helper;

import com.demo.digitalproduct.exception.IllegalUUIDException;

import java.util.UUID;

public class UuidHelper {

    public static UUID getStringFromUUID(String str) {
        try {
           return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            throw new IllegalUUIDException();
        }
    }
}
