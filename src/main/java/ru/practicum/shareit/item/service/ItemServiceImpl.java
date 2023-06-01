package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemDtoDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemDtoRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDtoRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto postItemDto(ItemDto itemDto, Long userId) {
        checkIfUserExists(userId, userRepository.findAll());
        return itemRepository.postItemDto(itemDto, userId);
    }

    @Override
    public ItemDto patchItemDto(Long itemId, ItemDto itemDto, Long userId) {
        //checkIfUserExists(userId, userRepository.getUsers());
        checkIfUserAndItemExists(userId, itemId, itemRepository.getAllItemsDtoWithUsersIds());
        return itemRepository.patchItemDto(itemId, itemDto, userId);
    }

    @Override
    public ItemDto getItemDto(Long itemId) {
        return itemRepository.getItemDto(itemId);
    }

    @Override
    public List<ItemDto> getAllItemsDtoByUser(Long userId) {
        checkIfUserExists(userId, userRepository.findAll());
        return itemRepository.getAllItemsDtoByUser(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> foundBySearch = new ArrayList<>();

        if (!text.isBlank()) {
            foundBySearch = itemRepository.getAllItemsDto().values().stream()
                    .filter(itemDto -> itemDto.getName().toLowerCase().contains(text.toLowerCase())
                            || itemDto.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(itemDto -> itemDto.getAvailable() == true)
                    .collect(Collectors.toList());
        }

        return foundBySearch;
    }

    @Override
    public void checkIfUserExists(Long userId, List<User> users) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return;
            }
        }

        throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
    }

    @Override
    public void checkIfUserAndItemExists(Long userId, Long itemDtoId, Map<Long, Map<Long, ItemDto>> items) {
        if (!items.containsKey(userId)) {
            throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
        }

        Map<Long, ItemDto> itemsDto = items.get(userId);
        if (!itemsDto.containsKey(itemDtoId)) {
            throw new ItemDtoDoesNotExistException("Вещи с указанным id не существует.");
        }
    }
}