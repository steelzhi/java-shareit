package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.actions.Actions;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

@Data
@AllArgsConstructor
@Builder
public class BookingAction {
    private Actions action;
    private ResponseEntity<Object> lastResponse;
    private long userId;
    private long ownerId;
    private String bookingStatus;
    private Integer from;
    private Integer size;
    private BookItemRequestDto requestDto;
    private long bookingId;
    private Boolean approved;
}
