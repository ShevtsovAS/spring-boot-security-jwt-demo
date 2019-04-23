package com.spring.boot.security.jwt.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@MappedSuperclass
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractBaseEntity<ID extends Number> implements Persistable<ID> {

    public static final int START_SEQ = 100000;

    @Id
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    protected ID id;

    @Override
    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    @Override
    public String toString() {
        return String.format("Entity %s (%s)", getClass().getName(), id);
    }
}
