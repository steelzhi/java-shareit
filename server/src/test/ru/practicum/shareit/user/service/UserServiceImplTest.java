package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

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

    UserDto userDto1 = UserMapper.mapToUserDto(user1);

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@user.ru")
            .build();

    List<User> users = List.of(user1, user2);

    List<UserDto> userDtos = UserMapper.mapToUserDto(users);

    @Test
    void postUser() {
        Mockito.when(userRepository.save(user1))
                .thenReturn(user1);

        UserDto postedUserDto1 = userService.postUserDto(userDto1);
        assertEquals(userDto1, postedUserDto1, "Выгруженный из БД пользователь не совпадает с первоначальным");

        Mockito.verify(userRepository, Mockito.times(1)).save(user1);
    }

    @Test
    void getUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(users);

        List<UserDto> returnedUserDtos = userService.getUserDtos();
        assertThat(returnedUserDtos, equalTo(userDtos));

        Mockito.verify(userRepository, Mockito.atMost(1)).findAll();
    }

    @Test
    void getUser() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        UserDto returnedUserDto1 = userService.getUserDto(1L);
        assertThat(returnedUserDto1, equalTo(userDto1));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getUserWithNotExisingId() {
        Mockito.when(userRepository.findById(100L))
                .thenThrow(new UserDoesNotExistException("Пользователя с id = 100 не существует"));

        UserDoesNotExistException userDoesNotExistException =
                assertThrows(UserDoesNotExistException.class, () -> userService.getUserDto(100L));
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

        UserDto patchedUserDto2 = UserMapper.mapToUserDto(patchedUser2);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        UserDto returnedPatchedUserDto2 = userService.patchUserDto(2L, patchedUserDto2);
        assertThat(patchedUserDto2, equalTo(returnedPatchedUserDto2));
        Mockito.verify(userRepository, Mockito.times(1)).save(patchedUser2);
    }

    @Test
    void deleteUser() {
        userService.deleteUserDto(1L);
        userService.deleteUserDto(2L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(2L);
        Mockito.verify(userRepository, Mockito.never()).deleteById(2000L);
    }
}
