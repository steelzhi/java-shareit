package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> getUsers() {
        return userService.getUserDtos();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.getUserDto(id);
    }

    @PostMapping()
    public UserDto postUser(@RequestBody UserDto userDto) {
        return userService.postUserDto(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable long id, @RequestBody UserDto userDto) {
        return userService.patchUserDto(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUserDto(id);
    }
}