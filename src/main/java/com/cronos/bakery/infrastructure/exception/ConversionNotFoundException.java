package com.cronos.bakery.infrastructure.exception;

public class ConversionNotFoundException extends RuntimeException {
    public ConversionNotFoundException(String message) {
        super(message);
    }
}
