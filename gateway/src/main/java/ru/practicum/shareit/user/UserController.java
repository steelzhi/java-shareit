package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.actions.Actions;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;
    private UserAction lastAction;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getId() == 0L) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = userClient.getUsers();
        lastAction = UserAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .id(0L)
                .build();

        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserDto(@PathVariable long id) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getId() == id) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = userClient.getUserDto(id);
        lastAction = UserAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .id(id)
                .build();

        return result;
    }

    @PostMapping()
    public ResponseEntity<Object> postUserDto(@RequestBody @Valid UserDto userDto) {
        ResponseEntity<Object> result = userClient.postUserDto(userDto);
        lastAction = UserAction.builder()
                .action(Actions.POST)
                .lastResponse(result)
                .userDto(userDto)
                .build();

        return result;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUserDto(@PathVariable long id, @RequestBody UserDto userDto) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.PATCH)
                    && userDto.equals(lastAction.getUserDto())
                    && lastAction.getId() == id) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = userClient.patchUserDto(id, userDto);
        lastAction = UserAction.builder()
                .action(Actions.PATCH)
                .lastResponse(result)
                .userDto(userDto)
                .id(id)
                .build();

        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserDto(@PathVariable long id) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.DELETE)
                    && lastAction.getId() == id) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = userClient.deleteUserDto(id);

        lastAction = UserAction.builder()
                .action(Actions.DELETE)
                .lastResponse(result)
                .id(id)
                .build();

        return result;
    }
}
