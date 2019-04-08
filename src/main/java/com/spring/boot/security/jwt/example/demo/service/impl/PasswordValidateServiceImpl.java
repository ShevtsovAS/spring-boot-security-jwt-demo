package com.spring.boot.security.jwt.example.demo.service.impl;

import com.spring.boot.security.jwt.example.demo.service.PasswordValidateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PasswordValidateServiceImpl implements PasswordValidateService {

    @Value("${spring.security.password.pattern}")
    private String passwordPattern;

    @Override
    public boolean isValid(String password) {
        if (StringUtils.isBlank(passwordPattern)) {
            return true;
        }
        return password.matches(passwordPattern);
    }

    @Override
    public boolean invalid(String password) {
        return !isValid(password);
    }
}
