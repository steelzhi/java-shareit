package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoResponseForItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto mapToItemDto(
            Item item,
            BookingDtoResponseForItemDto lastBookingDto,
            BookingDtoResponseForItemDto nextBookingDto,
            List<Comment> comments) {
        ItemDto itemDto = null;
        if (item != null) {
            itemDto = new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    UserMapper.mapToUserDto(item.getOwner()),
                    item.getRequestId(),
                    lastBookingDto,
                    nextBookingDto,
                    CommentMapper.mapToCommentDto(comments)
            );
        }
        return itemDto;
    }

    public static ItemDtoForSearch mapToItemDtoForSearch(Item item) {
        ItemDtoForSearch itemDtoForSearch = null;
        if (item != null) {
            itemDtoForSearch = new ItemDtoForSearch(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    UserMapper.mapToUserDto(item.getOwner()),
                    item.getRequestId()
            );
        }
        return itemDtoForSearch;
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = null;
        if (itemDto != null) {
            Long id = itemDto.getId() != null ? itemDto.getId() : null;
            item = new Item(
                    id,
                    itemDto.getName(),
                    itemDto.getDescription(),
                    itemDto.getAvailable(),
                    UserMapper.mapToUser(itemDto.getOwner()),
                    null);
        }
        return item;
    }
}