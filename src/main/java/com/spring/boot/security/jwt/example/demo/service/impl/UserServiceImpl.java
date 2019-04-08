package com.spring.boot.security.jwt.example.demo.service.impl;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.IncorrectPasswordException;
import com.spring.boot.security.jwt.example.demo.model.users.UserExistsException;
import com.spring.boot.security.jwt.example.demo.repository.UserRepository;
import com.spring.boot.security.jwt.example.demo.service.PasswordValidateService;
import com.spring.boot.security.jwt.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_EXISTS_MESSAGE = "Пользователь с таким именем уже существует!";
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь с именем %s не найден!";
    private static final String INCORRECT_CURRENT_PASSWORD_MESSAGE = "Вы ввели неверный текущий пароль!";

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final PasswordValidateService passwordValidateService;

    @Value("${spring.security.password.description}")
    private String passwordDescription;

    @Override
    public User create(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new UserExistsException(USER_EXISTS_MESSAGE);
        });

        if (passwordValidateService.invalid(user.getPassword())) {
            throw new IncorrectPasswordException(passwordDescription);
        }

        user.setId(null);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUserRoles(String username, Set<Role> roles) {
        User user = findUser(username);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public void delete(String username) {
        userRepository.delete(findUser(username));
    }

    @Override
    public User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username)));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = findUser(username);

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IncorrectPasswordException(INCORRECT_CURRENT_PASSWORD_MESSAGE);
        }

        if (passwordValidateService.invalid(newPassword)) {
            throw new IncorrectPasswordException(passwordDescription);
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
