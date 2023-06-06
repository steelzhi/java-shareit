package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {

    Item postItemDto(Item item, Long userId);

    Item patchItemDto(Long itemId, Item item, Long userId);

    ItemDto getItemDtoById(Long itemId, Long userId);

    List<ItemDto> getAllItemsDtoByUser(Long userId);

    List<Item> searchItems(String text);

    CommentDto postComment(Long itemId, Comment comment, Long userId);
}