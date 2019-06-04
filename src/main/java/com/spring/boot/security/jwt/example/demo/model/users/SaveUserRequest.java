package com.spring.boot.security.jwt.example.demo.model.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserRequest {
    private Long userId;
    private String username;
    private String password;
    private Set<String> roles;
    private boolean active;
}
