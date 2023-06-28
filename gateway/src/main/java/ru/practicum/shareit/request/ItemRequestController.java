package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.IncorrectPaginationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> postItemRequestDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.postItemRequestDto(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestDtosMadeByRequester(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllRequestDtosMadeByRequester(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getPagedRequestsMadeByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        if ((from == null && size == null) || (from >= 0 && size > 0)) {
            return itemRequestClient.getPagedRequestDtosMadeByOtherUsers(userId, from, size);
        }

        throw new IncorrectPaginationException("Введены некорректные параметры для пагинации");
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestDto(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return itemRequestClient.getRequestDto(userId, requestId);
    }
}