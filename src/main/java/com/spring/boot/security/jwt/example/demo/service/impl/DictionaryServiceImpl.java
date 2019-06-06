package com.spring.boot.security.jwt.example.demo.service.impl;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.model.Dictionary;
import com.spring.boot.security.jwt.example.demo.repository.RoleRepository;
import com.spring.boot.security.jwt.example.demo.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final RoleRepository roleRepository;
    private final static Map<String, String> ROLES_DICTIONARY;

    static {
        ROLES_DICTIONARY = Stream.of(
                new AbstractMap.SimpleEntry<>("ROLE_USER", "User"),
                new AbstractMap.SimpleEntry<>("ROLE_ADMIN", "Administrator")
        ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Dictionary getRolesDictionary() {
        return new Dictionary(roleRepository.findAll().stream()
                .map(Role::getName)
                .map(this::getRoleProperty)
                .collect(toList()));
    }

    private Dictionary.DictionaryProperty getRoleProperty(@NotNull String roleName) {
        String roleValue = ROLES_DICTIONARY.get(roleName);
        return Dictionary.DictionaryProperty.builder()
                .name(roleName)
                .value(roleValue == null ? roleName : roleValue)
                .build();
    }
}
