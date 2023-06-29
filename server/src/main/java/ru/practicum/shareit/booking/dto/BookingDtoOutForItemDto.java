package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoOutForItemDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoForSearch item;
    private Long bookerId;
    private BookingStatus status;
}