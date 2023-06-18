package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@user.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@user.ru")
            .build();

    List<User> users = List.of(user1, user2);

    @SneakyThrows
    @Test
    void getUsers() {
        Mockito.when(userService.getUsers())
                .thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).getUsers();

    }

    @SneakyThrows
    @Test
    void getUser() {
        Mockito.when(userService.getUser(1L))
                .thenReturn(user1);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).getUser(1L);

    }

    @SneakyThrows
    @Test
    void getUserWithIncorrectId() {
        Mockito.when(userService.getUser(100L))
                .thenThrow(new UserDoesNotExistException("Пользователя с id = 100 не существует"));

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound());

        Mockito.verify(userService, Mockito.times(1)).getUser(100L);

    }

    @SneakyThrows
    @Test
    void postUser() {
        Mockito.when(userService.postUser(user1))
                .thenReturn(user1);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).postUser(user1);

    }

/*    @SneakyThrows
    @Test
    void postUserWithDuplicateEmail() {
        User user1Duplicate = User.builder()
                .id(3L)
                .name("user1")
                .email("user1@user.ru")
                .build();

        Mockito.when(userService.postUser(user1Duplicate))
                .thenThrow(new DuplicateStatusException("Пользователь с таким email уже существует"));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(user1Duplicate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService, Mockito.times(1)).postUser(user1Duplicate);
    }*/

    @SneakyThrows
    @Test
    void patchUser() {
        User patchedUser2 = User.builder()
                .id(2L)
                .name("patchedUser2")
                .email("patchedUser2@user.ru")
                .build();

        Mockito.when(userService.patchUser(2L, patchedUser2))
                .thenReturn(patchedUser2);

        mockMvc.perform(patch("/users/2")
                        .content(objectMapper.writeValueAsBytes(patchedUser2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(patchedUser2.getName())))
                .andExpect(jsonPath("$.email", is(patchedUser2.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).patchUser(2L, patchedUser2);

    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/2"))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(2L);
    }
}