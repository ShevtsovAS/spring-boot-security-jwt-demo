package com.spring.boot.security.jwt.example.demo.model.users;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String newPasswordForCheck;
}
