package com.cronos.bakary.infrastructure.exception;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}
