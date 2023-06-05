package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto, Item itemDto, User booker) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                itemDto,
                booker,
                bookingDto.getStatus()
                );
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus());
    }

    public static List<BookingDto> mapToBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> mapToBookingDto(booking))
                .collect(Collectors.toList());
    }
}
