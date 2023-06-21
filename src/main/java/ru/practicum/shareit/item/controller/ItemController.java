package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto postItemDto(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.postItemDto(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItemDto(@PathVariable long itemId,
                                @RequestBody ItemDto itemDto,
                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.patchItemDto(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsDtoByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(value = "from", required = false) Integer from,
                                              @RequestParam(value = "size", required = false) Integer size) {
        return itemService.getAllItemsDtoByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDtoForSearch> searchItemDto(@PathParam("text") String text,
                                                @RequestParam(value = "from", required = false) Integer from,
                                                @RequestParam(value = "size", required = false) Integer size) {
        return itemService.searchItemDto(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postCommentDto(@PathVariable long itemId,
                                     @RequestBody CommentDto commentDto,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.postCommentDto(itemId, commentDto, userId);
    }
}