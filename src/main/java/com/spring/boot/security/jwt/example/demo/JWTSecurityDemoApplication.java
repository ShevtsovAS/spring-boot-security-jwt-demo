package com.spring.boot.security.jwt.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class JWTSecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JWTSecurityDemoApplication.class, args);
    }

}
