package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.actions.Actions;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
@Builder
public class UserAction {
    private Actions action;
    private ResponseEntity<Object> lastResponse;
    private long id;
    private UserDto userDto;
}