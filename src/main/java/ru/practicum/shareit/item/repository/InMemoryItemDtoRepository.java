/*
package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemDtoDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemDtoRepository implements ItemDtoRepository {
    private final Map<Long, Map<Long, ItemDto>> items = new HashMap<>();
    private static Long itemId = 1L;

    @Override
    public ItemDto postItemDto(ItemDto itemDto, Long userId) {
        itemDto.setId(itemId++);
        if (!items.containsKey(userId)) {
            items.put(userId, new HashMap<>());
        }
        items.get(userId).put(itemDto.getId(), itemDto);
        return itemDto;
    }

    @Override
    public ItemDto patchItemDto(Long itemId, ItemDto itemDto, Long userId) {
        ItemDto existingItemDto = items.get(userId).get(itemId);
        if (itemDto.getName() != null) {
            existingItemDto.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItemDto.setAvailable(itemDto.getAvailable());
        }

        return existingItemDto;
    }

    @Override
    public ItemDto getItemDto(Long itemId) {
        if (!getAllItemsDto().containsKey(itemId)) {
            throw new ItemDtoDoesNotExistException("Вещи с указанным id не существует.");
        }

        return getAllItemsDto().get(itemId);
    }

    @Override
    public List<ItemDto> getAllItemsDtoByUser(Long userId) {
        if (!items.containsKey(userId)) {
            throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
        }

        List<ItemDto> allItemsDtoByUser = new ArrayList<>(items.get(userId).values());
        return allItemsDtoByUser;
    }

    @Override
    public Map<Long, ItemDto> getAllItemsDto() {
        Map<Long, ItemDto> allItemsDto = new HashMap<>();
        for (Long userId : items.keySet()) {
            allItemsDto.putAll(items.get(userId));
        }
        return allItemsDto;
    }

    @Override
    public Map<Long, Map<Long, ItemDto>> getAllItemsDtoWithUsersIds() {
        return items;
    }
}*/
