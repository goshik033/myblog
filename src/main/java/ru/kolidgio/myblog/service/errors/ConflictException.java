package ru.kolidgio.myblog.service.errors;

public class ConflictException extends RuntimeException {
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
