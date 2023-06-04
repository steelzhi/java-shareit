package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemDtoDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistOrDoesNotHaveAnyItemsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemDtoRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDtoRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto postItemDto(ItemDto itemDto, Long userId) {
        checkIfUserExists(userId, userRepository.findAll());
        itemDto.setOwner(userRepository.findById(userId).get());
        return itemRepository.save(itemDto);
    }

    @Override
    public ItemDto patchItemDto(Long itemId, ItemDto itemDto, Long userId) {
        checkIfUserAndItemExists(userId, itemId);
        ItemDto existingItemDto = getItemDto(itemId);
        Long ownerId = null;
        if (itemDto.getOwner() != null) {
            ownerId = itemDto.getOwner().getId();
        }
        itemRepository.patchItemDto(itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequest(),
                ownerId);

        itemDto.setId(itemId);
        if (itemDto.getName() == null) {
            itemDto.setName(existingItemDto.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(existingItemDto.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(existingItemDto.getAvailable());
        }
        if (itemDto.getRequest() == null) {
            itemDto.setRequest(existingItemDto.getRequest());
        }
        if (itemDto.getOwner() == null) {
            itemDto.setOwner(existingItemDto.getOwner());
        }
        return itemDto;
    }

    @Override
    public ItemDto getItemDto(Long itemId) {
        List<ItemDto> itemDtos = itemRepository.findAll();
        for (ItemDto itemDto : itemDtos) {
            if (itemDto.getId() == itemId) {
                return itemDto;
            }
        }

        throw new ItemDtoDoesNotExistException("Вещи с id = " + itemId + " не существует.");
    }

    @Override
    public List<ItemDto> getAllItemsDtoByUser(Long userId) {
        checkIfUserExists(userId, userRepository.findAll());
        try {
            return itemRepository.findAllByOwnerId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> foundBySearch = new ArrayList<>();

        if (!text.isBlank()) {
            foundBySearch = itemRepository.findAll().stream()
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

    public void checkIfUserAndItemExists(Long userId, Long itemDtoId) {
        List<ItemDto> userItemsDto = getAllItemsDtoByUser(userId);
        if (userItemsDto.isEmpty()) {
            throw new UserDoesNotExistOrDoesNotHaveAnyItemsException(
                    "Пользователя с указанным id не существует либо пользователь не добавил ни одной вещи.");
        }
        for (ItemDto itemDto : userItemsDto) {
            if (itemDto.getId() == itemDtoId) {
                return;
            }
        }

        throw new ItemDtoDoesNotExistException("Вещи с указанным id не существует.");
    }
}