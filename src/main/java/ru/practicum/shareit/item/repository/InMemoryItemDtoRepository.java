package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemDtoDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utility.Checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemDtoRepository implements ItemDtoRepository {
    private final Map<Long, List<ItemDto>> items = new HashMap<>();
    private static Long itemId = 1L;

    @Override
    public ItemDto postItemDto(ItemDto itemDto, Long userId) {
        itemDto.setId(itemId++);
        if (!items.containsKey(userId)) {
            items.put(userId, new ArrayList<>());
        }
        items.get(userId).add(itemDto);
        return itemDto;
    }

    @Override
    public ItemDto patchItemDto(Long itemId, ItemDto itemDto, Long userId) {
        Checker.checkIfUserAndItemExists(userId, itemId, items);

        ItemDto existingItemDto = items.get(userId).stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst().get();

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
        for (ItemDto itemDto : getAllItemsDto()) {
            if (itemDto.getId().equals(itemId)) {
                return itemDto;
            }
        }

        throw new ItemDtoDoesNotExistException("Вещи с указанным id не существует.");
    }

    @Override
    public List<ItemDto> getAllItemsDtoByUser(Long userId) {
        if (items.containsKey(userId)) {
            return items.get(userId);
        }

        throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
    }

    @Override
    public List<ItemDto> getAllItemsDto() {
        List<ItemDto> allItemsDto = new ArrayList<>();
        for (Long userId : items.keySet()) {
            allItemsDto.addAll(items.get(userId));
        }
        return allItemsDto;
    }
}