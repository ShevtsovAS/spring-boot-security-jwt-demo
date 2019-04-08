package com.spring.boot.security.jwt.example.demo.model.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.boot.security.jwt.example.demo.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private boolean success;
    private String error;
    private User user;
}
