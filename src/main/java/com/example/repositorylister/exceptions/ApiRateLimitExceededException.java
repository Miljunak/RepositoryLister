package com.example.repositorylister.exceptions;

public class ApiRateLimitExceededException extends RuntimeException {
    public ApiRateLimitExceededException(String message) {
        super(message);
    }
}
