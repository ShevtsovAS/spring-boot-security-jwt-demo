package com.spring.boot.security.jwt.example.demo.model.users;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
