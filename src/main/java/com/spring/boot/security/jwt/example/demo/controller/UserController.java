package com.spring.boot.security.jwt.example.demo.controller;

import com.spring.boot.security.jwt.example.demo.aspect.LogExecutionTime;
import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.*;
import com.spring.boot.security.jwt.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static com.spring.boot.security.jwt.example.demo.controller.UserController.USERS_API;

@Slf4j
@CrossOrigin
@Controller
@RequestMapping(USERS_API)
@RequiredArgsConstructor
public class UserController {
    static final String USERS_API = "/api/v1/users";

    private final UserService userService;

    @LogExecutionTime
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> findUser(@PathVariable String username) {
        try {
            User user = userService.findUser(username);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @LogExecutionTime
    @PostMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> createRole(@RequestBody CreateRoleRequest request) {
        try {
            Role role = userService.createRole(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(role);
        } catch (RoleExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @LogExecutionTime
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        try {
            User created = userService.create(request);
            created.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (UserExistsException | IncorrectPasswordException | RoleNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @LogExecutionTime
    @PutMapping("/{username}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> updateUserRoles(@PathVariable String username,
                                                @RequestBody Set<String> roleNames) {
        try {
            User user = userService.updateUserRoles(username, roleNames);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException | RoleNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @LogExecutionTime
    @PutMapping("/{username}/deactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> deactivateUser(@PathVariable String username) {
        try {
            User user = userService.deactivate(username);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @LogExecutionTime
    @PutMapping("/{username}/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> activateUser(@PathVariable String username) {
        try {
            User user = userService.activate(username);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @LogExecutionTime
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(@PathVariable String username) {
        try {
            userService.delete(username);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @LogExecutionTime
    @GetMapping("/current")
    public ResponseEntity<User> currentUser(Authentication authentication) {
        try {
            User user = userService.findUser(authentication.getName());
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @LogExecutionTime
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/current/change-password")
    public void changePassword(@RequestBody ChangePasswordRequest request,
                               Authentication authentication) {
        try {
            if (!StringUtils.equals(request.getNewPassword(), request.getNewPasswordForCheck())) {
                throw new IncorrectPasswordException("Введёные новые пароли не совпадают!");
            }
            userService.changePassword(authentication.getName(), request.getOldPassword(), request.getNewPassword());
        } catch (IncorrectPasswordException | UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

}
