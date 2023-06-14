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
        return itemService.postItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item patchItem(@PathVariable long itemId,
                          @RequestBody Item item,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.patchItem(itemId, item, userId);
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
    public List<Item> searchItems(@PathParam("text") String text,
                                  @RequestParam(value = "from", required = false) Integer from,
                                  @RequestParam(value = "size", required = false) Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@PathVariable long itemId,
                                  @RequestBody Comment comment,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.postComment(itemId, comment, userId);
    }
}