package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutForController;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.util.List;

public interface BookingService {

    BookingDtoOutForController createBookingDto(BookingDtoIn bookingDto, long userId);

    BookingDtoOutForController patchBookingDtoWithUpdatedStatus(long bookingId, long userId, Boolean approved);

    BookingDtoOutForController getBookingDto(long bookingId, long userId);

    List<BookingDtoOutForController> getAllBookingDtosByUser(
            long userId,
            BookingStatus bookingStatus,
            Integer from,
            Integer size);

    List<BookingDtoOutForController> getAllBookingDtosForUserItems(
            long userId,
            BookingStatus bookingStatus,
            Integer from,
            Integer size);
}
