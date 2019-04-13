package com.spring.boot.security.jwt.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.security.jwt.example.demo.domain.Role;
import com.spring.boot.security.jwt.example.demo.domain.User;
import com.spring.boot.security.jwt.example.demo.model.users.ApplicationUser;
import com.spring.boot.security.jwt.example.demo.model.users.ChangePasswordRequest;
import com.spring.boot.security.jwt.example.demo.model.users.UserResponse;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.spring.boot.security.jwt.example.demo.controller.UserController.USERS_API;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private static final String TEST_USER_NAME = "testUser";
    private static final String TEST_USER_PASSWORD = "password123";
    private static final String TEST_USER_NEW_PASSWORD = "newPassword123";
    private static final String TEST_USER_NEW_ENCODED_PASSWORD = "$2a$10$ziEanFy98/vPGQ3f8ODI8.OS2Ox3/jo6Cc1rJ0P7tMRFnYN8BMsQ6";
    private static final String TEST_USER_ENCODED_PASSWORD = "$2a$10$wschGsPiIyObJxCRRQRMSesPO73pcXVkKB/KoA4QP4WCZ9TKNOALG";
    private static final String TEST_CREATED_USER_NAME = "createUser";
    private static final String TEST_CREATED_USER_ENCODED_PASSWORD = "$2a$10$fNBKepQgnCkm.MSTbH2RYOwokWCBBYgHZsK4mnkBhIZ2w1.L3iA2.";

    private static final User testUser = User.builder()
            .id(123L)
            .username(TEST_USER_NAME)
            .password(TEST_USER_ENCODED_PASSWORD)
            .roles(Collections.singleton(Role.USER))
            .active(true)
            .build();

    private static final User testUserToSave = new User(TEST_CREATED_USER_NAME, TEST_USER_PASSWORD, Collections.singleton(Role.USER));

    private static final User testCreatedUser = User.builder()
            .id(124L)
            .username(TEST_CREATED_USER_NAME)
            .password(TEST_CREATED_USER_ENCODED_PASSWORD)
            .roles(Collections.singleton(Role.USER))
            .active(true)
            .build();

    private static final User testUserToSaveWithEncodedPassword = User.builder()
            .username(testUserToSave.getUsername())
            .password(testCreatedUser.getPassword())
            .roles(testUserToSave.getRoles())
            .active(testUserToSave.getActive())
            .build();

    @MockBean
    UserRepository userRepository;

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

        when(bCryptPasswordEncoder.encode(testUserToSave.getPassword())).thenReturn(testCreatedUser.getPassword());
        when(bCryptPasswordEncoder.encode(TEST_USER_NEW_PASSWORD)).thenReturn(TEST_USER_NEW_ENCODED_PASSWORD);
        when(bCryptPasswordEncoder.matches(TEST_USER_PASSWORD, testUser.getPassword())).thenReturn(true);
        when(userRepository.findByUsername(TEST_USER_NAME)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(TEST_CREATED_USER_NAME)).thenReturn(Optional.empty());
        when(userRepository.save(testUserToSaveWithEncodedPassword)).thenReturn(testCreatedUser);
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

        checkResult(result, testUser);
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
                .content(mapper.writeValueAsString(testUserToSave)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResult(result, testCreatedUser);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void createUserBadRequest() throws Exception {
        MvcResult result = mvc.perform(post(USERS_API)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResultFailed(result);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void createUserForbidden() throws Exception {
        mvc.perform(post(USERS_API)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(testUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void updateUserRoles() throws Exception {
        Set<Role> roles = Stream.of(Role.USER, Role.ADMIN).collect(toSet());
        User expectedUser = User.builder()
                .id(testUser.getId())
                .username(testUser.getUsername())
                .password(testUser.getPassword())
                .active(testUser.getActive())
                .roles(roles)
                .build();

        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        MvcResult result = mvc.perform(put(USERS_API + "/" + TEST_USER_NAME + "/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(roles)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResult(result, expectedUser);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void updateUserRolesBadRequest() throws Exception {
        MvcResult result = mvc.perform(put(USERS_API + "/notExistUser/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(Collections.singleton(Role.ADMIN))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResultFailed(result);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void updateUserRolesForbidden() throws Exception {
        mvc.perform(put(USERS_API + "/notExistUser/roles")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(Collections.singleton(Role.ADMIN))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deactivateUser() throws Exception {
        User expectedUser = User.builder()
                .id(testUser.getId())
                .username(testUser.getUsername())
                .password(testUser.getPassword())
                .active(false)
                .roles(testUser.getRoles())
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
        MvcResult result = mvc.perform(put(USERS_API + "/notExistUser/deactivate")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResultFailed(result);
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

        checkResult(result, testUser);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void activateUserBadRequest() throws Exception {
        MvcResult result = mvc.perform(put(USERS_API + "/notExistUser/activate")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResultFailed(result);
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
        MvcResult result = mvc.perform(delete(USERS_API + "/" + TEST_USER_NAME))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()))
                .andReturn();

        checkResult(result);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deleteUserBadRequest() throws Exception {
        MvcResult result = mvc.perform(delete(USERS_API + "/notExistUser"))
                .andExpect(status().isBadRequest())
                .andReturn();

        checkResultFailed(result);
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

        checkResult(result, testUser);
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
                .id(testUser.getId())
                .username(testUser.getUsername())
                .password(TEST_USER_NEW_ENCODED_PASSWORD)
                .active(testUser.getActive())
                .roles(testUser.getRoles())
                .build();

        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(TEST_USER_PASSWORD)
                .newPassword(TEST_USER_NEW_PASSWORD)
                .newPasswordForCheck(TEST_USER_NEW_PASSWORD)
                .build();

        MvcResult result = mvc.perform(patch(USERS_API + "/current/change-password")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResult(result);
    }

    @Test
    @WithMockUser(TEST_USER_NAME)
    public void changePasswordIncorrectCurrentPassword() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("IncorrectPassword")
                .newPassword(TEST_USER_NEW_PASSWORD)
                .newPasswordForCheck(TEST_USER_NEW_PASSWORD)
                .build();

        MvcResult result = mvc.perform(patch(USERS_API + "/current/change-password")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResultFailed(result);
    }

    @Test
    @WithMockUser(TEST_USER_NAME)
    public void changePasswordBadNewPassword() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(TEST_USER_PASSWORD)
                .newPassword("12345")
                .newPasswordForCheck("12345")
                .build();

        MvcResult result = mvc.perform(patch(USERS_API + "/current/change-password")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();

        checkResultFailed(result);
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

    private void checkResult(MvcResult result) throws Exception {
        checkResult(result, null);
    }

    private void checkResult(MvcResult result, User expectedUser) throws Exception {
        String content = result.getResponse().getContentAsString();
        UserResponse response = mapper.readValue(content, UserResponse.class);

        assertNotNull(response);
        assertNull(response.getError());
        assertTrue(response.isSuccess());

        if (expectedUser != null) {
            assertNotNull(response.getUser());
            User userFromResponse = response.getUser();

            assertNotNull(userFromResponse.getId());
            assertNull(userFromResponse.getPassword());
            assertThat(userFromResponse.getActive(), is(expectedUser.getActive()));
            assertThat(userFromResponse.getUsername(), is(expectedUser.getUsername()));
            assertEquals(userFromResponse.getRoles(), expectedUser.getRoles());
        }
    }

    private void checkResultFailed(MvcResult result) throws IOException {
        String content = result.getResponse().getContentAsString();
        UserResponse response = mapper.readValue(content, UserResponse.class);

        assertNotNull(response);
        assertNotNull(response.getError());
        assertFalse(response.isSuccess());
    }
}