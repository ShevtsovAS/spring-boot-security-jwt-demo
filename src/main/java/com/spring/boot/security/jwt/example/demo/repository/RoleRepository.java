package com.spring.boot.security.jwt.example.demo.repository;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findFirstByName(String name);

    Set<Role> findAllByNameIn(Set<String> roleNames);
}
