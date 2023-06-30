package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoOutForItemDtoTest {

    @Autowired
    JacksonTester<BookingDtoResponseForItemDto> json1;

    User user1 = User.builder()
            .id(1L)
            .build();

    User user2 = User.builder()
            .id(2L)
            .build();

    Item item1 = Item.builder()
            .id(1L)
            .owner(user1)
            .build();

    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    Booking booking = Booking.builder()
            .id(1L)
            .start(now)
            .end(now.plusDays(1L))
            .item(item1)
            .booker(user2)
            .status(BookingStatus.WAITING)
            .build();

    @Test
    @SneakyThrows
    void testBookingDtoOutForItemDto() {
        BookingDtoResponseForItemDto bookingDtoOutForItemDto = BookingMapper.mapToBookingDtoOutForItemDto(booking);

        JsonContent<BookingDtoResponseForItemDto> result = json1.write(bookingDtoOutForItemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(booking.getId()));
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(Math.toIntExact(booking.getItem().getId()));
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(Math.toIntExact(booking.getBooker().getId()));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(now.plusDays(1L).format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}