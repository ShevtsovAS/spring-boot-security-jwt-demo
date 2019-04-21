package com.spring.boot.security.jwt.example.demo.service.impl;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.*;
import com.spring.boot.security.jwt.example.demo.repository.RoleRepository;
import com.spring.boot.security.jwt.example.demo.repository.UserRepository;
import com.spring.boot.security.jwt.example.demo.service.PasswordValidateService;
import com.spring.boot.security.jwt.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_EXISTS_MESSAGE = "Пользователь с таким именем уже существует!";
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь с именем %s не найден!";
    private static final String INCORRECT_CURRENT_PASSWORD_MESSAGE = "Вы ввели неверный текущий пароль!";
    private static final String ROLE_DOES_NOT_EXIST = "Роль с именем %s не существует";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final PasswordValidateService passwordValidateService;

    @Value("${spring.security.password.description}")
    private String passwordDescription;

    @Override
    public User create(CreateUserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new UserExistsException(USER_EXISTS_MESSAGE);
        });

        if (passwordValidateService.invalid(request.getPassword())) {
            throw new IncorrectPasswordException(passwordDescription);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .roles(getRolesByNames(request.getRoles()))
                .build();

        return userRepository.save(user);
    }

    @Override
    public User updateUserRoles(String username, Set<String> roleNames) {
        User user = findUser(username);
        Set<Role> roles = getRolesByNames(roleNames);
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

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public User deactivate(String username) {
        User user = findUser(username);

        if (!user.getActive()) {
            return user;
        }

        user.setActive(false);
        return userRepository.save(user);
    }

    @Override
    public User activate(String username) {
        User user = findUser(username);

        if (user.getActive()) {
            return user;
        }

        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public Role createRole(CreateRoleRequest request) {
        roleRepository.findFirstByName(request.getRoleName()).ifPresent(role -> {
            throw new RoleExistsException(String.format("Роль с именем %s уже существует", request.getRoleName()));
        });

        return roleRepository.save(Role.builder()
                .name(request.getRoleName())
                .build());
    }

    private Set<Role> getRolesByNames(Set<String> roleNames) {
        Set<Role> roles = roleRepository.findAllByNameIn(roleNames);
        roleNames = roleNames.stream()
                .filter(roleName -> roles.stream().noneMatch(role -> StringUtils.equals(role.getName(), roleName)))
                .collect(toSet());

        if (!roleNames.isEmpty()) {
            throw new RoleNotFoundException(String.format(ROLE_DOES_NOT_EXIST, roleNames.iterator().next()));
        }

        return roles;
    }
}
