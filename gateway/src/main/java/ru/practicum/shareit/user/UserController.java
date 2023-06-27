package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserDto(@PathVariable long id) {
        return userClient.getUserDto(id);
    }

    @PostMapping()
    public ResponseEntity<Object> postUserDto(@RequestBody @Valid UserDto userDto) {
        return userClient.postUserDto(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUserDto(@PathVariable long id, @RequestBody UserDto userDto) {
        return userClient.patchUserDto(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserDto(@PathVariable long id) {
        return userClient.deleteUserDto(id);
    }
}
