package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemDtoRepository {
    ItemDto postItemDto(ItemDto itemDto, Long userId);

    ItemDto patchItemDto(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemDto(Long itemId);

    List<ItemDto> getAllItemsDtoByUser(Long userId);

    Map<Long, ItemDto> getAllItemsDto();

    Map<Long, Map<Long, ItemDto>> getAllItemsDtoWithUsersIds();
}