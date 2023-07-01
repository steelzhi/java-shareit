package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForController;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    private BookingMapper() {
    }

    public static Booking mapToBooking(BookingDtoRequest bookingDto, Item item, User booker) {
        Booking booking = null;
        if (bookingDto != null) {
            booking = new Booking(bookingDto.getId(),
                    bookingDto.getStart(),
                    bookingDto.getEnd(),
                    item,
                    booker,
                    bookingDto.getStatus()
            );
        }
        return booking;
    }

    public static BookingDtoResponseForController mapToBookingDtoOutForController(Booking booking) {
        BookingDtoResponseForController bookingDtoOutForController = null;
        if (booking != null) {
            bookingDtoOutForController = new BookingDtoResponseForController(booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    ItemMapper.mapToItemDtoForSearch(booking.getItem()),
                    UserMapper.mapToUserDto(booking.getBooker()),
                    booking.getStatus());
        }
        return bookingDtoOutForController;
    }


    public static BookingDtoResponseForItemDto mapToBookingDtoOutForItemDto(Booking booking) {
        BookingDtoResponseForItemDto bookingDtoOutForItemDto = null;
        if (booking != null) {
            bookingDtoOutForItemDto = new BookingDtoResponseForItemDto(booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    ItemMapper.mapToItemDtoForSearch(booking.getItem()),
                    booking.getBooker().getId(),
                    booking.getStatus());
        }
        return bookingDtoOutForItemDto;
    }


    public static List<BookingDtoResponseForController> mapToBookingDtoOutForController(List<Booking> bookings) {
        if (bookings != null) {
            return bookings.stream()
                    .map(booking -> mapToBookingDtoOutForController(booking))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public static List<BookingDtoResponseForItemDto> mapToBookingDtoOutForItemDto(List<Booking> bookings) {
        if (bookings != null) {
            return bookings.stream()
                    .map(booking -> mapToBookingDtoOutForItemDto(booking))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
