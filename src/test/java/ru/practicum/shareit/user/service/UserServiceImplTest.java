package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

/*    @Test
    void getUsers() {
    }

    @Test
    void getUser() {
    }*/

    @Test
    void postUser() {
        UserService userService = new UserServiceImpl(userRepository);
        User user = new User(null, "aa", "aa@bb.cc");
        Mockito.when(userService.postUser(user))
                .thenReturn(user);

        User postedUser = userService.postUser(user);
        assertEquals(user, postedUser, "Выгруженный из БД пользователь не совпадает с первоначальным");

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

/*    @Test
    void patchUser() {
    }

    @Test
    void deleteUser() {
    }*/
}