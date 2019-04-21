package com.spring.boot.security.jwt.example.demo.model.users;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
