package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto postItemDto(ItemDto itemDto, Long userId);

    ItemDto patchItemDto(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemDto(Long itemId);

    List<ItemDto> getAllItemsDtoByUser(Long userId);

    List<ItemDto> searchItems(String text);
}
