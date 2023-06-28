package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.Pagination;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookingDtosByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String bookingStatus,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        BookingState state = BookingState.from(bookingStatus)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + bookingStatus));

        log.info("Get bookings with state {}, userId={}, from={}, size={}", bookingStatus, userId, from, size);
        return bookingClient.getAllBookingDtosByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingDtosForUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String bookingStatus,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        BookingState state = BookingState.from(bookingStatus)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + bookingStatus));

        log.info("Get bookings with state {}, userId={}, from={}, size={}", bookingStatus, userId, from, size);
        return bookingClient.getAllBookingDtosForUserItems(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBookingDtoWithUpdatedStatus(@PathVariable long bookingId,
                                                                   @RequestParam("approved") Boolean approved,
                                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.patchBookingDtoWithUpdatedStatus(bookingId, approved, userId);
    }
}
