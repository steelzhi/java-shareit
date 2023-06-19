package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<ItemDtoForSearch> itemDtos2) {
        return new ItemRequestDto(
                itemRequest.getId(),
                UserMapper.mapToUserDto(itemRequest.getRequester()),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemDtos2
        );
    }

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                UserMapper.mapToUser(itemRequestDto.getRequesterDto()),
                itemRequestDto.getDescription(),
                itemRequestDto.getCreated()
        );
    }
}
