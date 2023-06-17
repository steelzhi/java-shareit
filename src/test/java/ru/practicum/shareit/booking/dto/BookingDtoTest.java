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
class BookingDtoTest {

    @Autowired
    JacksonTester<BookingDto> json1;

    @Autowired
    JacksonTester<Booking> json2;

    Item item1 = Item.builder()
            .id(1L)
            .build();

    User user2 = User.builder()
            .id(1L)
            .build();

    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    Booking booking = Booking.builder()
            .id(1L)
            .item(item1)
            .booker(user2)
            .start(now)
            .end(now.plusDays(1L))
            .status(BookingStatus.WAITING)
            .build();

    BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);


    @Test
    @SneakyThrows
    void testBookingDto() {
        JsonContent<BookingDto> result = json1.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(Math.toIntExact(booking.getItem().getId()));
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(booking.getId()));
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(Math.toIntExact(booking.getBooker().getId()));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(now.plusDays(1L).format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    @SneakyThrows
    void testBooking() {
        Booking mappedFromBookingDto = BookingMapper.mapToBooking(bookingDto, item1, user2);

        JsonContent<Booking> result = json2.write(mappedFromBookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(bookingDto.getId()));
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(Math.toIntExact(bookingDto.getItemId()));
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(Math.toIntExact(bookingDto.getBookerId()));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(now.plusDays(1L).format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}