package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoOutForItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Component
public class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto mapToItemDto(
            Item item, BookingDtoOutForItemDto lastBookingDto, BookingDtoOutForItemDto nextBookingDto, List<Comment> comments) {
        return new ItemDto(
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

    public static ItemDtoForSearch mapToItemDtoForSearch(Item item) {
        return new ItemDtoForSearch(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                UserMapper.mapToUserDto(item.getOwner()),
                item.getRequestId()
        );
    }

    public static Item mapToItem(ItemDto itemDto) {
        Long id = itemDto.getId() != null ? itemDto.getId() : null;
        return new Item(
                id,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                UserMapper.mapToUser(itemDto.getOwner()),
                null);
    }
}