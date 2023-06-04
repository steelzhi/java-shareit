package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.util.List;

public interface BookingService {

    Booking createBooking(BookingDto bookingDto, Long userId);

    Booking getBookingWithNewStatus(Long bookingId, Boolean approved);

    Booking getBooking(Long bookingId, Long userId);

    List<Booking> getAllBookingsByUser(Long userId, String bookingStatus);

    List<Booking> getAllBookingsForUserItems(Long userId, String bookingStatus);
}
