package com.spring.boot.security.jwt.example.demo.controller;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.ChangePasswordRequest;
import com.spring.boot.security.jwt.example.demo.model.users.IncorrectPasswordException;
import com.spring.boot.security.jwt.example.demo.model.users.UserExistsException;
import com.spring.boot.security.jwt.example.demo.model.users.UserResponse;
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

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> findUser(@PathVariable String username) {
        try {
            User user = userService.findUser(username);
            user.setPassword(null);
            return ResponseEntity.ok(UserResponse.builder()
                    .success(true)
                    .user(user)
                    .build());
        } catch (UsernameNotFoundException e) {
            return notFoundResponse(e);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
        try {
            User created = userService.create(user);
            created.setPassword(null);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(UserResponse.builder()
                            .success(true)
                            .user(created)
                            .build());
        } catch (UserExistsException | IncorrectPasswordException e) {
            return badRequestResponse(e);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @PutMapping("/{username}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> updateUserRoles(@PathVariable String username,
                                                        @RequestBody Set<Role> roles) {
        try {
            User user = userService.updateUserRoles(username, roles);
            user.setPassword(null);
            return ResponseEntity.ok(UserResponse.builder()
                    .success(true)
                    .user(user)
                    .build());
        } catch (UsernameNotFoundException e) {
            return badRequestResponse(e);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable String username) {
        try {
            userService.delete(username);
            return ResponseEntity.noContent().build();
        } catch (UsernameNotFoundException e) {
            return badRequestResponse(e);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<UserResponse> currentUser(Authentication authentication) {
        try {
            User user = userService.findUser(authentication.getName());
            user.setPassword(null);
            return ResponseEntity.ok(UserResponse.builder()
                    .success(true)
                    .user(user)
                    .build());
        } catch (UsernameNotFoundException e) {
            return badRequestResponse(e);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @PatchMapping("/current/change-password")
    public ResponseEntity<UserResponse> changePassword(@RequestBody ChangePasswordRequest request,
                                                       Authentication authentication) {
        try {
            if (!StringUtils.equals(request.getNewPassword(), request.getNewPasswordForCheck())) {
                throw new IncorrectPasswordException("Введёные новые пароли не совпадают!");
            }
            userService.changePassword(authentication.getName(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(UserResponse.builder()
                    .success(true)
                    .build());
        } catch (IncorrectPasswordException | UsernameNotFoundException e) {
            return badRequestResponse(e);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    private ResponseEntity<UserResponse> badRequestResponse(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .badRequest()
                .body(UserResponse.builder()
                        .error(e.getMessage())
                        .build());
    }

    private ResponseEntity<UserResponse> notFoundResponse(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(UserResponse.builder()
                        .error(e.getMessage())
                        .build());
    }

    private ResponseEntity<UserResponse> errorResponse(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(UserResponse.builder()
                        .error(e.getMessage())
                        .build());
    }

}
