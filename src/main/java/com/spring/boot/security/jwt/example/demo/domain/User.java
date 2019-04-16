package com.spring.boot.security.jwt.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.boot.security.jwt.example.demo.model.AbstractBaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class User extends AbstractBaseEntity<Long> {
    private String username;
    private String password;
    private Boolean active = true;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User(String username, String password, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Builder
    public User(Long id, String username, String password, Boolean active, Set<Role> roles) {
        super(id);
        this.username = username;
        this.password = password;
        this.active = active;
        this.roles = roles;
    }
}
