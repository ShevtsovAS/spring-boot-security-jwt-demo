package com.spring.boot.security.jwt.example.demo.repository;

import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.spring.boot.security.jwt.example.demo.model.AbstractBaseEntity.START_SEQ;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:database/data.sql")
public class RepositoryTest {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String TEST_ROLE = "ROLE_TEST";
    private static final String USER_NAME = "user";
    private static final String ADMIN_NAME = "admin";
    private static final String CREATE_USER_NAME = "test";
    private static final String CREATE_USER_PASSWORD = "$2a$10$wschGsPiIyObJxCRRQRMSesPO73pcXVkKB/KoA4QP4WCZ9TKNOALG";

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void findAllRoles() {
        List<Role> roles = roleRepository.findAll();
        assertThat(roles, hasSize(2));
    }

    @Test
    public void findRoleByName() {
        String roleName = roleRepository.findFirstByName(ROLE_USER).map(Role::getName).orElse(null);
        assertThat(roleName, is(ROLE_USER));
    }

    @Test
    public void createRole() {
        Role role = roleRepository.save(new Role(TEST_ROLE));

        assertNotNull(role);
        assertThat(role.getId(), is((long) START_SEQ));
        assertThat(role.getName(), is(TEST_ROLE));
    }

    @Test
    public void deleteRole() {
        User user = userRepository.findByUsername(ADMIN_NAME).orElse(null);
        userRepository.delete(user);

        Role role = roleRepository.findFirstByName(ROLE_ADMIN).orElse(null);
        assertNotNull(role);

        roleRepository.delete(role);

        List<Role> roles = roleRepository.findAll();
        assertThat(roles, hasSize(1));
    }

    @Test
    public void deleteUsedRole() {
        User user = userRepository.findByUsername(USER_NAME).orElse(null);
        userRepository.delete(user);

        Role role = roleRepository.findFirstByName(ROLE_USER).orElse(null);
        assertNotNull(role);

        // Attempt to remove a role that is used by the administrator
        expectedException.expect(DataIntegrityViolationException.class);
        expectedException.expectMessage("ConstraintViolationException");
        roleRepository.delete(role);
        roleRepository.flush();
    }

    @Test
    public void findAllUsers() {
        List<User> users = userRepository.findAll();
        assertThat(users, hasSize(2));
    }

    @Test
    public void findUserByName() {
        String userName = userRepository.findByUsername(USER_NAME).map(User::getUsername).orElse(null);
        assertThat(userName, is(USER_NAME));
    }

    @Test
    public void createUser() {
        Set<Role> roles = Collections.singleton(new Role(1L, ROLE_USER));
        User user = userRepository.save(User.builder()
                .username(CREATE_USER_NAME)
                .password(CREATE_USER_PASSWORD)
                .roles(roles)
                .build());

        assertNotNull(user);
        assertThat(user.getId(), is((long) (START_SEQ + 1)));
        assertThat(user.getUsername(), is(CREATE_USER_NAME));
        assertThat(user.getPassword(), is(CREATE_USER_PASSWORD));
        assertEquals(user.getRoles(), roles);
    }

    @Test
    public void deleteUser() {
        User user = userRepository.findByUsername(USER_NAME).orElse(null);
        assertNotNull(user);

        userRepository.delete(user);
        List<User> users = userRepository.findAll();
        assertThat(users, hasSize(1));
    }
}