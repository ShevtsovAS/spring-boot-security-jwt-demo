package com.spring.boot.security.jwt.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Data
@Component
@Profile("security")
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JWTProperties {
    private String secret;
    private long expirationTime;
    private String tokenPrefix;
    private String headerString;
    private String loginPath;
}
