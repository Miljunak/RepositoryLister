package com.example.repositorylister.exceptions;

import java.util.concurrent.ExecutionException;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}

