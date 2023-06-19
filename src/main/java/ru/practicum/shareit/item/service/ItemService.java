package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;

import java.util.List;

@Service
public interface ItemService {

    ItemDto postItemDto(ItemDto itemDto, long userId);

    ItemDto patchItemDto(long itemId, ItemDto itemDto, long userId);

    ItemDto getItemDtoById(long itemId, long userId);

    List<ItemDto> getAllItemsDtoByUser(long userId, Integer from, Integer size);

    List<ItemDtoForSearch> searchItemDto(String text, Integer from, Integer size);

    CommentDto postCommentDto(long itemId, CommentDto commentDto, long userId);
}