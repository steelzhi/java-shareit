package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest postItemRequest(@RequestHeader("X-Sharer-User-Id") long userId, ItemRequest itemRequest) {
        return itemRequestService.postItemRequest(userId, itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsMadeByRequester(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllRequestsMadeByRequester(userId);

    }
}