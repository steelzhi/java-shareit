package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto postItemDto(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.postItemDto(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItemDto(@PathVariable Long itemId,
                                @RequestBody ItemDto itemDto,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.patchItemDto(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDto(@PathVariable Long itemId) {
        return itemService.getItemDto(itemId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsDtoByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@PathParam("text") String text) {
        return itemService.searchItems(text);
    }
}