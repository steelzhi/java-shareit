package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.util.Pagination;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> postItemDto(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.postItemDto(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItemDto(@PathVariable long itemId,
                                @RequestBody ItemDto itemDto,
                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.patchItemDto(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemDtoById(@PathVariable long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsDtoByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(value = "from", required = false) Integer from,
                                              @RequestParam(value = "size", required = false) Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);
        return itemClient.getAllItemsDtoByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemDto(@PathParam("text") String text,
                                                @RequestParam(value = "from", required = false) Integer from,
                                                @RequestParam(value = "size", required = false) Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);
        return itemClient.searchItemDto(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postCommentDto(@PathVariable long itemId,
                                     @RequestBody CommentDto commentDto,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.postCommentDto(itemId, commentDto, userId);
    }
}