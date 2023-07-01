package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto postItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.postItemRequestDto(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsMadeByRequester(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllRequestDtosMadeByRequester(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getPagedRequestsMadeByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        return itemRequestService.getPagedRequestDtosMadeByOtherUsers(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return itemRequestService.getRequestDto(userId, requestId);
    }
}