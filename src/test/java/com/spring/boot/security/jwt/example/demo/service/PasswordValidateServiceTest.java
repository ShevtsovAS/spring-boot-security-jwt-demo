package com.spring.boot.security.jwt.example.demo.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class PasswordValidateServiceTest {

    private static final String VALID_PASSWORD = "password123";
    private static final String SHORT_PASSWORD = "pass";
    private static final String SIMPLE_PASSWORD = "password";

    @Autowired
    PasswordValidateService passwordValidateService;

    @Test
    public void validPassword() {
        assertTrue(passwordValidateService.isValid(VALID_PASSWORD));
    }

    @Test
    public void shortPassword() {
        assertTrue(passwordValidateService.invalid(SHORT_PASSWORD));
    }

    @Test
    public void simplePassword() {
        assertTrue(passwordValidateService.invalid(SIMPLE_PASSWORD));
    }

}