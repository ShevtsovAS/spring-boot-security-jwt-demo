package com.spring.boot.security.jwt.example.demo.service;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;

import java.util.Set;

public interface UserService {
    User create(User user);

    User updateUserRoles(String username, Set<Role> roles);

    void delete(String username);

    User findUser(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    User deactivate(String username);

    User activate(String username);
}
