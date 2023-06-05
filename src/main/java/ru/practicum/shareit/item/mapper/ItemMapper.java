package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemMapper {

    public static ItemDto mapToItemDtoForOwner(
            Item item, BookingDto lastBookingDto, BookingDto nextBookingDto) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest(),
                lastBookingDto,
                nextBookingDto
        );
    }

/*    public static ItemDto mapToItem(Item itemDto) {
        return new ItemDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest()
        );
    }*/
}