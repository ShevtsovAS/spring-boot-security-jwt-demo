package com.spring.boot.security.jwt.example.demo.service;

public interface PasswordValidateService {
    boolean isValid(String password);
    boolean invalid(String password);
}
