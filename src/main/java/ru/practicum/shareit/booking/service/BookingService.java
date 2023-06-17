package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(BookingDto bookingDto, long userId);

    Booking patchBookingWithUpdatedStatus(long bookingId, long userId, Boolean approved);

    Booking getBooking(long bookingId, long userId);

    List<Booking> getAllBookingsByUser(long userId, String bookingStatus, Integer from, Integer size);

    List<Booking> getAllBookingsForUserItems(long userId, String bookingStatus, Integer from, Integer size);
}
