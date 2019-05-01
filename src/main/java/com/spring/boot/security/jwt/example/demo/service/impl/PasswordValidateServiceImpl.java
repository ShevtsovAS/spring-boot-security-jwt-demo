package com.spring.boot.security.jwt.example.demo.service.impl;

import com.spring.boot.security.jwt.example.demo.config.SecurityProperties;
import com.spring.boot.security.jwt.example.demo.service.PasswordValidateService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordValidateServiceImpl implements PasswordValidateService {

    private final SecurityProperties securityProperties;

    @Override
    public boolean isValid(String password) {
        if (StringUtils.isBlank(securityProperties.getPattern())) {
            return true;
        }
        return password.matches(securityProperties.getPattern());
    }

    @Override
    public boolean invalid(String password) {
        return !isValid(password);
    }
}
