package com.spring.boot.security.jwt.example.demo.model.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserRequest {
    private Long userId;
    @NotNull(message = "Username can't be null")
    private String username;
    @NotNull(message = "Password can't be null")
    @Size(min = 8, message = "Password too short")
    private String password;
    private Set<String> roles;
    private boolean active;
}
