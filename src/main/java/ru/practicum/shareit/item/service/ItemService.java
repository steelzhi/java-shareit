package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {

    Item postItemDto(Item item, long userId);

    Item patchItemDto(long itemId, Item item, long userId);

    ItemDto getItemDtoById(long itemId, long userId);

    List<ItemDto> getAllItemsDtoByUser(long userId);

    List<Item> searchItems(String text);

    CommentDto postComment(long itemId, Comment comment, long userId);
}