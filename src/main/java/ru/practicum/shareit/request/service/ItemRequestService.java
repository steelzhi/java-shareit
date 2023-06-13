package ru.practicum.shareit.request.service;

import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest postItemRequest(long userId, ItemRequest itemRequest);

    List<ItemRequestDto> getAllRequestsMadeByRequester(long userId);
}
