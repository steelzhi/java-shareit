package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.actions.Actions;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.DuplicateStatusExceptionInController;
import ru.practicum.shareit.util.Pagination;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private BookingAction lastAction;

    @GetMapping
    public ResponseEntity<Object> getAllBookingDtosByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String bookingStatus,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        BookingState state = BookingState.from(bookingStatus)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + bookingStatus));

        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getUserId() == userId
                    && lastAction.getBookingStatus().equals(bookingStatus)
                    && from.equals(lastAction.getFrom())
                    && size.equals(lastAction.getSize())) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = bookingClient.getAllBookingDtosByUser(userId, state, from, size);
        lastAction = BookingAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .userId(userId)
                .bookingStatus(bookingStatus)
                .from(from)
                .size(size)
                .build();

        log.info("Get bookings with state {}, userId={}, from={}, size={}", bookingStatus, userId, from, size);
        return result;
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

        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getOwnerId() == userId
                    && lastAction.getBookingStatus().equals(bookingStatus)
                    && from.equals(lastAction.getFrom())
                    && size.equals(lastAction.getSize())) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = bookingClient.getAllBookingDtosForUserItems(userId, state, from, size);
        lastAction = BookingAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .ownerId(userId)
                .bookingStatus(bookingStatus)
                .from(from)
                .size(size)
                .build();

        log.info("Get bookings with state {}, userId={}, from={}, size={}", bookingStatus, userId, from, size);
        return result;
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        ResponseEntity<Object> result = bookingClient.bookItem(userId, requestDto);
        lastAction = BookingAction.builder()
                .action(Actions.POST)
                .lastResponse(result)
                .userId(userId)
                .requestDto(requestDto)
                .build();

        log.info("Creating booking {}, userId={}", requestDto, userId);
        return result;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getOwnerId() == userId
                    && lastAction.getBookingId() == bookingId) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = bookingClient.getBooking(userId, bookingId);
        lastAction = BookingAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .ownerId(userId)
                .bookingId(bookingId)
                .build();
        log.info("Get booking {}, userId={}", bookingId, userId);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBookingDtoWithUpdatedStatus(@PathVariable long bookingId,
                                                                   @RequestParam("approved") Boolean approved,
                                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.PATCH)
                    && lastAction.getBookingId() == bookingId
                    && approved.equals(lastAction.getApproved())
                    && lastAction.getUserId() == userId) {
                throw new DuplicateStatusExceptionInController("Данный статус уже установлен ранее.");
            }
        }

        ResponseEntity<Object> result = bookingClient.patchBookingDtoWithUpdatedStatus(bookingId, approved, userId);
        lastAction = BookingAction.builder()
                .action(Actions.PATCH)
                .lastResponse(result)
                .bookingId(bookingId)
                .approved(approved)
                .userId(userId)
                .build();

        return result;
    }
}
