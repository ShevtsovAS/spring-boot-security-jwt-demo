package com.spring.boot.security.jwt.example.demo.model.users;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String message) {
        super(message);
    }
}
