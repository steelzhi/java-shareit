package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto postItemRequestDto(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllRequestDtosMadeByRequester(long userId);

    List<ItemRequestDto> getPagedRequestDtosMadeByOtherUsers(long userId, Integer from, Integer size);

    ItemRequestDto getRequestDto(long userId, long requestId);
}
