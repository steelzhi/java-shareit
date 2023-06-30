package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoInTest {

    @Autowired
    JacksonTester<BookingDtoRequest> json1;

    Item item1 = Item.builder()
            .id(1L)
            .build();

    User user2 = User.builder()
            .id(1L)
            .build();

    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    BookingDtoRequest bookingDtoIn = BookingDtoRequest.builder()
            .id(1L)
            .start(now)
            .end(now.plusDays(1L))
            .itemId(item1.getId())
            .bookerId(user2.getId())
            .build();

    @Test
    @SneakyThrows
    void testBookingDto() {
        JsonContent<BookingDtoRequest> result = json1.write(bookingDtoIn);

        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(Math.toIntExact(bookingDtoIn.getItemId()));
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(bookingDtoIn.getId()));
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(Math.toIntExact(bookingDtoIn.getBookerId()));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(now.plusDays(1L).format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
    }
}
