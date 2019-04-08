package com.spring.boot.security.jwt.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JWTProperties {
    private String secret;
    private long expirationTime;
    private String tokenPrefix;
    private String headerString;
}
