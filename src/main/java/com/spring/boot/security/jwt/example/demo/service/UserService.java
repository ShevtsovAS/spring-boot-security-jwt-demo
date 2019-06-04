package com.spring.boot.security.jwt.example.demo.service;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.CreateRoleRequest;
import com.spring.boot.security.jwt.example.demo.model.users.SaveUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {
    User create(SaveUserRequest request);

    User updateUserRoles(String username, Set<String> roleNames);

    void delete(String username);

    User findUser(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    User deactivate(String username);

    User activate(String username);

    Role createRole(CreateRoleRequest request);

    Page<User> findAll(Pageable pageable);

    User getUser(Long userId);

    User update(SaveUserRequest user);

    void delete(Long userId);
}
