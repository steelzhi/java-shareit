package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface ItemService {

    Item postItemDto(Item item, Long userId);

    Item patchItemDto(Long itemId, Item item, Long userId);

    ItemDtoForOwner getItemDtoById(Long itemId, Long userId);

    List<Item> getAllItemsByUser(Long userId);

    List<Item> searchItems(String text);

    void checkIfUserExists(Long userId, List<User> users);

    void checkIfUserAndItemExists(Long userId, Long itemDtoId);
}