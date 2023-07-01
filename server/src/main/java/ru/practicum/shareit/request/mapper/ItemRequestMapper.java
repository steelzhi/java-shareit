package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<ItemDtoForSearch> itemDtos) {
        ItemRequestDto itemRequestDto = null;
        if (itemRequest != null) {
            itemRequestDto = new ItemRequestDto(
                    itemRequest.getId(),
                    UserMapper.mapToUserDto(itemRequest.getRequester()),
                    itemRequest.getDescription(),
                    itemRequest.getCreated(),
                    itemDtos
            );
        }
        return itemRequestDto;
    }

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        if (itemRequestDto != null) {
            itemRequest = new ItemRequest(
                    itemRequestDto.getId(),
                    UserMapper.mapToUser(itemRequestDto.getRequesterDto()),
                    itemRequestDto.getDescription(),
                    itemRequestDto.getCreated()
            );
        }
        return itemRequest;
    }
}
