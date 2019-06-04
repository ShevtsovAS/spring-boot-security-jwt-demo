package com.spring.boot.security.jwt.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.ApplicationUser;
import com.spring.boot.security.jwt.example.demo.model.users.ChangePasswordRequest;
import com.spring.boot.security.jwt.example.demo.model.users.CreateRoleRequest;
import com.spring.boot.security.jwt.example.demo.model.users.SaveUserRequest;
import com.spring.boot.security.jwt.example.demo.repository.RoleRepository;
import com.spring.boot.security.jwt.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.spring.boot.security.jwt.example.demo.controller.UserControllerRest.USERS_API;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("test,security,ssl")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerRestTest {

    private static final String TEST_USER_NAME = "testUser";
    private static final String TEST_USER_PASSWORD = "password123";
    private static final String TEST_USER_ENCODED_PASSWORD = "$2a$10$wschGsPiIyObJxCRRQRMSesPO73pcXVkKB/KoA4QP4WCZ9TKNOALG";
    private static final String TEST_USER_NEW_PASSWORD = "newPassword123";
    private static final String TEST_USER_NEW_ENCODED_PASSWORD = "$2a$10$ziEanFy98/vPGQ3f8ODI8.OS2Ox3/jo6Cc1rJ0P7tMRFnYN8BMsQ6";
    private static final String TEST_CREATED_USER_NAME = "createUser";
    private static final String TEST_CREATED_USER_PASSWORD = "password123";
    private static final String TEST_CREATED_USER_ENCODED_PASSWORD = "$2a$10$fNBKepQgnCkm.MSTbH2RYOwokWCBBYgHZsK4mnkBhIZ2w1.L3iA2.";
    private static final Role EXISTED_ROLE = new Role(1L, "ROLE_USER");
    private static final Set<Role> TEST_USER_ROLES = Collections.singleton(EXISTED_ROLE);

    private static final SaveUserRequest SAVE_USER_REQUEST = SaveUserRequest.builder()
            .username(TEST_CREATED_USER_NAME)
            .password(TEST_CREATED_USER_PASSWORD)
            .roles(Collections.singleton("ROLE_USER"))
            .active(true)
            .build();

    private static final CreateRoleRequest CREATE_ROLE_REQUEST = CreateRoleRequest.builder()
            .roleName("ROLE_NEW_USER")
            .build();

    private static final User TEST_USER = User.builder()
            .id(123L)
            .username(TEST_USER_NAME)
            .password(TEST_USER_ENCODED_PASSWORD)
            .roles(TEST_USER_ROLES)
            .build();

    private static final User TEST_USER_TO_SAVE = new User(TEST_CREATED_USER_NAME, TEST_USER_PASSWORD, TEST_USER_ROLES);

    private static final User TEST_CREATED_USER = User.builder()
            .id(124L)
            .username(TEST_CREATED_USER_NAME)
            .password(TEST_CREATED_USER_ENCODED_PASSWORD)
            .roles(TEST_USER_ROLES)
            .active(true)
            .build();

    private static final User testUserToSaveWithEncodedPassword = User.builder()
            .username(TEST_USER_TO_SAVE.getUsername())
            .password(TEST_CREATED_USER.getPassword())
            .roles(TEST_USER_TO_SAVE.getRoles())
            .active(TEST_USER_TO_SAVE.getActive())
            .build();

    @MockBean
    UserRepository userRepository;

    @MockBean
    RoleRepository roleRepository;

    @SpyBean
    PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        when(bCryptPasswordEncoder.encode(TEST_USER_TO_SAVE.getPassword())).thenReturn(TEST_CREATED_USER.getPassword());
        when(bCryptPasswordEncoder.encode(TEST_USER_NEW_PASSWORD)).thenReturn(TEST_USER_NEW_ENCODED_PASSWORD);
        when(bCryptPasswordEncoder.matches(TEST_USER_PASSWORD, TEST_USER.getPassword())).thenReturn(true);
        when(userRepository.findByUsername(TEST_USER_NAME)).thenReturn(Optional.of(TEST_USER));
        when(userRepository.findByUsername(TEST_CREATED_USER_NAME)).thenReturn(Optional.empty());
        when(userRepository.save(testUserToSaveWithEncodedPassword)).thenReturn(TEST_CREATED_USER);
        when(roleRepository.findAllByNameIn(SAVE_USER_REQUEST.getRoles())).thenReturn(TEST_USER_ROLES);
        when(roleRepository.findFirstByName(EXISTED_ROLE.getName())).thenReturn(Optional.of(EXISTED_ROLE));
    }

    @Test
    public void login() throws Exception {
        ApplicationUser user = ApplicationUser.builder()
                .username(TEST_USER_NAME)
                .password(TEST_USER_PASSWORD)
                .build();

        mvc.perform(post("/login")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Authorization"));
    }

    @Test
    public void loginFailedWrongPassword() throws Exception {
        ApplicationUser user = ApplicationUser.builder()
                .username(TEST_USER_NAME)
                .password("wrongPassword")
                .build();

        mvc.perform(post("/login")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Authorization"));
    }

    @Test
    public void loginFailedUserNotExists() throws Exception {
        ApplicationUser user = ApplicationUser.builder()
                .username("NotExistUser")
                .password("password")
                .build();

        mvc.perform(post("/login")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Authorization"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void findUser() throws Exception {
        MvcResult result = mvc.perform(get(USERS_API + "/" + TEST_USER_NAME)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();
        checkResult(result, TEST_USER);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void findUserNotFound() throws Exception {
        mvc.perform(get(USERS_API + "/notExistUser"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void findUserForbidden() throws Exception {
        mvc.perform(get(USERS_API + "/" + TEST_USER_NAME))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void createUser() throws Exception {
        MvcResult result = mvc.perform(post(USERS_API)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(SAVE_USER_REQUEST)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResult(result, TEST_CREATED_USER);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void createUserBadRequest() throws Exception {
        SaveUserRequest badSaveUserRequest = SaveUserRequest.builder()
                .username(TEST_USER_NAME)
                .password(TEST_USER_PASSWORD)
                .roles(Collections.singleton("ROLE_USER"))
                .build();
        mvc.perform(post(USERS_API)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(badSaveUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void createUserForbidden() throws Exception {
        mvc.perform(post(USERS_API)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(SAVE_USER_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void createRole() throws Exception {
        Role roleToCreate = new Role(CREATE_ROLE_REQUEST.getRoleName());
        Role createdRole = new Role(123L, roleToCreate.getName());
        when(roleRepository.save(roleToCreate)).thenReturn(createdRole);
        mvc.perform(post(USERS_API + "/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(CREATE_ROLE_REQUEST)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void createRoleBadRequest() throws Exception {
        mvc.perform(post(USERS_API + "/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(EXISTED_ROLE.getName())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void createRoleForbidden() throws Exception {
        mvc.perform(post(USERS_API + "/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(EXISTED_ROLE.getName())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void updateUserRoles() throws Exception {
        Set<String> newRoleNames = Stream.of("ROLE_USER", "ROLE_ADMIN").collect(toSet());
        Set<Role> newRoles = Stream.of(new Role(1L, "ROLE_USER"), new Role(2L, "ROLE_ADMIN")).collect(toSet());
        when(roleRepository.findAllByNameIn(newRoleNames)).thenReturn(newRoles);
        User expectedUser = User.builder()
                .id(TEST_USER.getId())
                .username(TEST_USER.getUsername())
                .password(TEST_USER.getPassword())
                .active(TEST_USER.getActive())
                .roles(newRoles)
                .build();

        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        MvcResult result = mvc.perform(put(USERS_API + "/" + TEST_USER_NAME + "/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(newRoleNames)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResult(result, expectedUser);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void updateUserRolesBadRequest() throws Exception {
        mvc.perform(put(USERS_API + "/notExistUser/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(Collections.singleton("ROLE_USER"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void updateUserRolesForbidden() throws Exception {
        mvc.perform(put(USERS_API + "/notExistUser/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(Collections.singleton("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deactivateUser() throws Exception {
        User expectedUser = User.builder()
                .id(TEST_USER.getId())
                .username(TEST_USER.getUsername())
                .password(TEST_USER.getPassword())
                .active(false)
                .roles(TEST_USER.getRoles())
                .build();

        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        MvcResult result = mvc.perform(put(USERS_API + "/" + TEST_USER_NAME + "/deactivate")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResult(result, expectedUser);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deactivateUserBadRequest() throws Exception {
        mvc.perform(put(USERS_API + "/notExistUser/deactivate")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void deactivateUserForbidden() throws Exception {
        mvc.perform(put(USERS_API + "/notExistUser/deactivate"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void activateUser() throws Exception {
        MvcResult result = mvc.perform(put(USERS_API + "/" + TEST_USER_NAME + "/activate")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResult(result, TEST_USER);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void activateUserBadRequest() throws Exception {
        mvc.perform(put(USERS_API + "/notExistUser/activate")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void activateUserForbidden() throws Exception {
        mvc.perform(put(USERS_API + "/notExistUser/activate"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deleteUser() throws Exception {
        mvc.perform(delete(USERS_API + "/" + TEST_USER_NAME))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deleteUserBadRequest() throws Exception {
        mvc.perform(delete(USERS_API + "/notExistUser"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void deleteUserForbidden() throws Exception {
        mvc.perform(delete(USERS_API + "/" + TEST_USER_NAME))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_NAME)
    public void currentUser() throws Exception {
        MvcResult result = mvc.perform(get(USERS_API + "/current")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResult(result, TEST_USER);
    }

    @Test
    public void currentUserForbidden() throws Exception {
        mvc.perform(get(USERS_API + "/current"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(TEST_USER_NAME)
    public void changePassword() throws Exception {
        User expectedUser = User.builder()
                .id(TEST_USER.getId())
                .username(TEST_USER.getUsername())
                .password(TEST_USER_NEW_ENCODED_PASSWORD)
                .active(TEST_USER.getActive())
                .roles(TEST_USER.getRoles())
                .build();

        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(TEST_USER_PASSWORD)
                .newPassword(TEST_USER_NEW_PASSWORD)
                .newPasswordForCheck(TEST_USER_NEW_PASSWORD)
                .build();

        mvc.perform(patch(USERS_API + "/current/change-password")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(TEST_USER_NAME)
    public void changePasswordIncorrectCurrentPassword() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("IncorrectPassword")
                .newPassword(TEST_USER_NEW_PASSWORD)
                .newPasswordForCheck(TEST_USER_NEW_PASSWORD)
                .build();

        mvc.perform(patch(USERS_API + "/current/change-password")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(TEST_USER_NAME)
    public void changePasswordBadNewPassword() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(TEST_USER_PASSWORD)
                .newPassword("12345")
                .newPasswordForCheck("12345")
                .build();

        mvc.perform(patch(USERS_API + "/current/change-password")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void changePasswordForbidden() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(TEST_USER_PASSWORD)
                .newPassword(TEST_USER_NEW_PASSWORD)
                .newPasswordForCheck(TEST_USER_NEW_PASSWORD)
                .build();

        mvc.perform(patch(USERS_API + "/current/change-password")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private void checkResult(MvcResult result, User expectedUser) throws Exception {
        String content = result.getResponse().getContentAsString();
        User user = mapper.readValue(content, User.class);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNull(user.getPassword());
        assertThat(user.getActive(), is(expectedUser.getActive()));
        assertThat(user.getUsername(), is(expectedUser.getUsername()));
        assertEquals(user.getRoles(), expectedUser.getRoles());
    }
}