package com.spring.boot.security.jwt.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.boot.security.jwt.example.demo.model.AbstractBaseEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends AbstractBaseEntity<Long> implements GrantedAuthority {

    @NotNull
    @Column(name = "role_name")
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role(@NotNull String name) {
        this.name = name;
    }

    public Role(Long aLong, @NotNull String name) {
        super(aLong);
        this.name = name;
    }

    @Builder
    public Role(Long id, @NotNull String name, Set<User> users) {
        super(id);
        this.name = name;
        this.users = users;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        return name;
    }
}
