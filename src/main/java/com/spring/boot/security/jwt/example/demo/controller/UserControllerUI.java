package com.spring.boot.security.jwt.example.demo.controller;

import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserControllerUI {

    private final UserService userService;

    @GetMapping
    public String userList(Model model, @PageableDefault(size = 20, sort = "username") Pageable pageable) {
        Page<User> userPage = userService.findAll(pageable);
        model.addAttribute("users", userPage.getContent());
        return "users/user-list";
    }
}
