package com.spring.boot.security.jwt.example.demo.model.users;

public class RoleExistsException extends RuntimeException {
    public RoleExistsException(String message) {
        super(message);
    }
}
