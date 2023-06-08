package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody BookingDto bookingDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking patchBookingWithUpdatedStatus(@PathVariable long bookingId,
                                                 @RequestParam("approved") Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.patchBookingWithUpdatedStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<Booking> getAllBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false) String bookingStatus) {
        return bookingService.getAllBookingsByUser(userId, bookingStatus);
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingsForUserItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false) String bookingStatus) {
        return bookingService.getAllBookingsForUserItems(userId, bookingStatus);
    }
}