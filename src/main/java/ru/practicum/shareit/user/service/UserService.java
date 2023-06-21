package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUserDtos();

    UserDto getUserDto(long userDtoId);

    UserDto postUserDto(UserDto userDto);

    UserDto patchUserDto(long id, UserDto userDto);

    void deleteUserDto(long userDtoId);
}