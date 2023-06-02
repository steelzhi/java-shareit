package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

@Service
public interface ItemService {

    ItemDto postItemDto(ItemDto itemDto, Long userId);

    ItemDto patchItemDto(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemDto(Long itemId);

    List<ItemDto> getAllItemsDtoByUser(Long userId);

    List<ItemDto> searchItems(String text);

    void checkIfUserExists(Long userId, List<User> users);

    void checkIfUserAndItemExists(Long userId, Long itemDtoId);
}