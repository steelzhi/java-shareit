package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = null;
        if (user != null) {
            userDto = new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        }
        return userDto;
    }

    public static User mapToUser(UserDto userDto) {
        User user = null;
        if (userDto != null) {
            user = new User(
                    userDto.getId(),
                    userDto.getName(),
                    userDto.getEmail()
            );
        }
        return user;
    }

    public static List<UserDto> mapToUserDto(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                userDtos.add(mapToUserDto(user));
            }
        }
        return userDtos;
    }
}