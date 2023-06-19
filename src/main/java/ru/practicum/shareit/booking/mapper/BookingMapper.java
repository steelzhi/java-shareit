package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutForController;
import ru.practicum.shareit.booking.dto.BookingDtoOutForItemDto;
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

    public static Booking mapToBooking(BookingDtoIn bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus()
        );
    }

    public static BookingDtoOutForController mapToBookingDtoOutForController(Booking booking) {
        return new BookingDtoOutForController(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.mapToItemDtoForSearch(booking.getItem()),
                UserMapper.mapToUserDto(booking.getBooker()),
                booking.getStatus());
    }


    public static BookingDtoOutForItemDto mapToBookingDtoOutForItemDto(Booking booking) {
        return new BookingDtoOutForItemDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.mapToItemDtoForSearch(booking.getItem()),
                booking.getBooker().getId(),
                booking.getStatus());
    }


    public static List<BookingDtoOutForController> mapToBookingDtoOutForController(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> mapToBookingDtoOutForController(booking))
                .collect(Collectors.toList());
    }

    public static List<BookingDtoOutForItemDto> mapToBookingDtoOutForItemDto(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> mapToBookingDtoOutForItemDto(booking))
                .collect(Collectors.toList());
    }
}
