/*
package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);

    UserService userService = new UserServiceImpl(userRepository);

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

    @Test
    void postUser() {
        Mockito.when(userService.postUser(user1))
                .thenReturn(user1);

        User postedUser1 = userService.postUser(user1);
        assertEquals(user1, postedUser1, "Выгруженный из БД пользователь не совпадает с первоначальным");

        Mockito.verify(userRepository, Mockito.times(1)).save(user1);
    }

    @Test
    void getUsers() {
        Mockito.when(userService.getUsers())
                .thenReturn(users);

        List<User> returnedUsers = userService.getUsers();
        assertThat(returnedUsers, equalTo(users));

        Mockito.verify(userRepository, Mockito.atMost(1)).findAll();
    }

    @Test
    void getUser() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        User returnedUser1 = userService.getUser(1L);
        assertThat(returnedUser1, equalTo(user1));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getUserWithNotExisingId() {
        Mockito.when(userRepository.findById(100L))
                .thenThrow(new UserDoesNotExistException("Пользователя с id = 100 не существует"));

        UserDoesNotExistException userDoesNotExistException =
                assertThrows(UserDoesNotExistException.class, () -> userService.getUser(100L));
        assertEquals(userDoesNotExistException.getMessage(), "Пользователя с id = 100 не существует");

        Mockito.verify(userRepository, Mockito.times(1)).findById(100L);
    }

    @Test
    void patchUser() {
        User patchedUser2 = User.builder()
                .id(2L)
                .name("patchedUser2")
                .email("patchedUser2@user.ru")
                .build();

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(userService.patchUser(2L, patchedUser2))
                .thenReturn(patchedUser2);

        User returnedPatchedUser2 = userService.patchUser(2L, patchedUser2);
        assertThat(patchedUser2, equalTo(returnedPatchedUser2));
        Mockito.verify(userRepository, Mockito.times(1)).save(patchedUser2);
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);
        userService.deleteUser(2L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(2L);
        Mockito.verify(userRepository, Mockito.never()).deleteById(2000L);
    }
}*/
