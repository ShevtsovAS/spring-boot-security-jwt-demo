package com.spring.boot.security.jwt.example.demo.controller;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.CreateUserRequest;
import com.spring.boot.security.jwt.example.demo.model.users.UpdateUserRequest;
import com.spring.boot.security.jwt.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.Collectors.toSet;

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

    @GetMapping("/add")
    public String addUser(Model model) {
        model.addAttribute("createRequest", CreateUserRequest.builder()
                .active(true)
                .build());
        return "users/user-form";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute CreateUserRequest createRequest) {
        userService.create(createRequest);
        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String editUser(@RequestParam Long userId, Model model) {
        User user = userService.getUser(userId);
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .userId(userId)
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(Role::getName).collect(toSet()))
                .active(user.getActive())
                .build();
        model.addAttribute("updateRequest", updateUserRequest);

        return "users/user-edit-form";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute UpdateUserRequest updateRequest) {
        userService.update(updateRequest);
        return "redirect:/users";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam Long userId) {
        userService.delete(userId);
        return "redirect:/users";
    }
}
