package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {

    Item postItem(Item item, long userId);

    Item patchItem(long itemId, Item item, long userId);

    ItemDto getItemDtoById(long itemId, long userId);

    List<ItemDto> getAllItemsDtoByUser(long userId, Integer from, Integer size);

    List<Item> searchItems(String text, Integer from, Integer size);

    CommentDto postComment(long itemId, Comment comment, long userId);
}