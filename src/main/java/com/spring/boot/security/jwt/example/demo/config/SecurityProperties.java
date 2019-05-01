package com.spring.boot.security.jwt.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.security.password")
public class SecurityProperties {
    private String pattern;
    private String description;
}
