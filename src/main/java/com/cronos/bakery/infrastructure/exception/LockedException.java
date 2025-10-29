package com.cronos.bakery.infrastructure.exception;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}
