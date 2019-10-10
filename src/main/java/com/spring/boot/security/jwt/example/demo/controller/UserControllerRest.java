package com.spring.boot.security.jwt.example.demo.controller;

import com.spring.boot.security.jwt.example.demo.aspect.LogExecutionTime;
import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.ChangePasswordRequest;
import com.spring.boot.security.jwt.example.demo.model.users.CreateRoleRequest;
import com.spring.boot.security.jwt.example.demo.model.users.IncorrectPasswordException;
import com.spring.boot.security.jwt.example.demo.model.users.SaveUserRequest;
import com.spring.boot.security.jwt.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

import static com.spring.boot.security.jwt.example.demo.controller.UserControllerRest.USERS_API;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(USERS_API)
@RequiredArgsConstructor
public class UserControllerRest {
    static final String USERS_API = "/api/v1/users";

    private final UserService userService;

    @LogExecutionTime
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<User>> getAll(@PageableDefault(size = 20, sort = "username") Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @LogExecutionTime
    @GetMapping("/{username}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> findUser(@PathVariable String username) {
        User user = userService.findUser(username);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @LogExecutionTime
    @PostMapping("/roles")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Role> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createRole(request));
    }

    @LogExecutionTime
    @PostMapping
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> createUser(@Valid @RequestBody SaveUserRequest request) {
        User created = userService.create(request);
        created.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @LogExecutionTime
    @PutMapping("/{userId}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> updateUser(@PathVariable Long userId,
                                           @RequestBody SaveUserRequest saveUserRequest) {
        saveUserRequest.setUserId(userId);
        User user = userService.update(saveUserRequest);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @LogExecutionTime
    @PutMapping("/{username}/roles")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> updateUserRoles(@PathVariable String username,
                                                @RequestBody Set<String> roleNames) {
        User user = userService.updateUserRoles(username, roleNames);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @LogExecutionTime
    @PutMapping("/{username}/deactivate")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> deactivateUser(@PathVariable String username) {
        User user = userService.deactivate(username);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @LogExecutionTime
    @PutMapping("/{username}/activate")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> activateUser(@PathVariable String username) {
        User user = userService.activate(username);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @LogExecutionTime
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{username}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Secured("ROLE_ADMIN")
    public void deleteUser(@PathVariable String username) {
        userService.delete(username);
    }

    @LogExecutionTime
    @GetMapping("/current")
    public ResponseEntity<User> currentUser(Authentication authentication) {
        User user = userService.findUser(authentication.getName());
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @LogExecutionTime
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/current/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request,
                               Authentication authentication) {
        if (!StringUtils.equals(request.getNewPassword(), request.getNewPasswordForCheck())) {
            throw new IncorrectPasswordException("Введёные новые пароли не совпадают!");
        }
        userService.changePassword(authentication.getName(), request.getOldPassword(), request.getNewPassword());
    }

}
