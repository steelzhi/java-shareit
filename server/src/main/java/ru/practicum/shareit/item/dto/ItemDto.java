package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private BookingDtoResponseForItemDto lastBooking;
    private BookingDtoResponseForItemDto nextBooking;
    private List<CommentDto> comments;
}