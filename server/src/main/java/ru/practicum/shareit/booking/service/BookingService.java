package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForController;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.util.List;

public interface BookingService {

    BookingDtoResponseForController createBookingDto(BookingDtoRequest bookingDto, long userId);

    BookingDtoResponseForController patchBookingDtoWithUpdatedStatus(long bookingId, long userId, Boolean approved);

    BookingDtoResponseForController getBookingDto(long bookingId, long userId);

    List<BookingDtoResponseForController> getAllBookingDtosByUser(
            long userId,
            BookingStatus bookingStatus,
            Integer from,
            Integer size);

    List<BookingDtoResponseForController> getAllBookingDtosForUserItems(
            long userId,
            BookingStatus bookingStatus,
            Integer from,
            Integer size);
}
