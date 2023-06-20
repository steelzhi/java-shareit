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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
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

    UserDto userDto1 = UserMapper.mapToUserDto(user1);

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@user.ru")
            .build();

    UserDto userDto2 = UserMapper.mapToUserDto(user2);

    List<User> users = List.of(user1, user2);

    List<UserDto> userDtos = UserMapper.mapToUserDto(users);

    @SneakyThrows
    @Test
    void getUsers() {
        Mockito.when(userService.getUserDtos())
                .thenReturn(userDtos);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).getUserDtos();

    }

    @SneakyThrows
    @Test
    void getUser() {
        Mockito.when(userService.getUserDto(1L))
                .thenReturn(userDto1);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).getUserDto(1L);

    }

    @SneakyThrows
    @Test
    void getUserWithIncorrectId() {
        Mockito.when(userService.getUserDto(100L))
                .thenThrow(new UserDoesNotExistException("Пользователя с id = 100 не существует"));

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound());

        Mockito.verify(userService, Mockito.times(1)).getUserDto(100L);

    }

    @SneakyThrows
    @Test
    void postUser() {
        Mockito.when(userService.postUserDto(userDto1))
                .thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).postUserDto(userDto1);

    }

    @SneakyThrows
    @Test
    void patchUser() {
        User patchedUser2 = User.builder()
                .id(2L)
                .name("patchedUser2")
                .email("patchedUser2@user.ru")
                .build();

        UserDto patchedUserDto2 = UserMapper.mapToUserDto(patchedUser2);


        Mockito.when(userService.patchUserDto(2L, patchedUserDto2))
                .thenReturn(patchedUserDto2);

        mockMvc.perform(patch("/users/2")
                        .content(objectMapper.writeValueAsBytes(patchedUserDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(patchedUserDto2.getName())))
                .andExpect(jsonPath("$.email", is(patchedUserDto2.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).patchUserDto(2L, patchedUserDto2);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/2"))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUserDto(2L);
    }
}
