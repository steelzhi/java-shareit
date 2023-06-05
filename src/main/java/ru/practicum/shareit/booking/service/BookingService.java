package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(ru.practicum.shareit.booking.dto.BookingDto bookingDto, Long userId);

    Booking patchBookingWithUpdatedStatus(Long bookingId, Long userId, Boolean approved);

    Booking getBooking(Long bookingId, Long userId);

    List<Booking> getAllBookingsByUser(Long userId, String bookingStatus);

    List<Booking> getAllBookingsForUserItems(Long userId, String bookingStatus);
}
