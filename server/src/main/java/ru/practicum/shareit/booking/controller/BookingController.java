package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponseForController createBooking(@RequestBody BookingDtoRequest bookingDto,
                                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBookingDto(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponseForController patchBookingWithUpdatedStatus(
            @PathVariable long bookingId,
            @RequestParam("approved") Boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.patchBookingDtoWithUpdatedStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponseForController getBooking(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingDto(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponseForController> getAllBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false) BookingStatus bookingStatus,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        return bookingService.getAllBookingDtosByUser(userId, bookingStatus, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponseForController> getAllBookingsForUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false) BookingStatus bookingStatus,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        return bookingService.getAllBookingDtosForUserItems(userId, bookingStatus, from, size);
    }
}