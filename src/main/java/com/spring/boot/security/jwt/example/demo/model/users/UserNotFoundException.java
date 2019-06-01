package com.spring.boot.security.jwt.example.demo.model.users;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
