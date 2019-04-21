package com.spring.boot.security.jwt.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.boot.security.jwt.example.demo.model.AbstractBaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class User extends AbstractBaseEntity<Long> {
    private String username;
    private String password;
    private Boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

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
        this.active = active != null ? active : true;
        this.roles = roles;
    }
}
