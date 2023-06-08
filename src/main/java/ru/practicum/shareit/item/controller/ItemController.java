package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
    public Item postItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.postItemDto(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item patchItem(@PathVariable long itemId,
                          @RequestBody Item item,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.patchItemDto(itemId, item, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsDtoByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsDtoByUser(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@PathParam("text") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@PathVariable long itemId,
                                  @RequestBody Comment comment,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.postComment(itemId, comment, userId);
    }
}