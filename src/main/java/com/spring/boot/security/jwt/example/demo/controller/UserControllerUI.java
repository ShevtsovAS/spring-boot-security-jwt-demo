package com.spring.boot.security.jwt.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@EnableOAuth2Sso
public class UserControllerUI {
    @GetMapping
    public String userList() {
        return "users/user-list";
    }
}
