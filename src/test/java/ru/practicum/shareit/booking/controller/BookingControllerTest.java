package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@user.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@user.ru")
            .build();

    List<User> users = List.of(user1, user2);

    Item item1 = Item.builder()
            .id(1L)
            .name("УШМ")
            .description("Углошлифовальная машина")
            .available(true)
            .owner(user1)
            .build();

    Item item2 = Item.builder()
            .id(2L)
            .name("Канистра")
            .description("Для ГСМ, V = 20 л")
            .available(true)
            .owner(user2)
            .build();

    Item item3 = Item.builder()
            .id(3L)
            .name("Зарядное устройство")
            .description("Для зарядки аккумуляторов")
            .available(false)
            .owner(user2)
            .build();

    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    Booking booking = Booking.builder()
            .id(1L)
            .item(item1)
            .booker(user2)
            .start(now.plusHours(1L))
            .end(now.plusDays(1L))
            .status(BookingStatus.WAITING)
            .build();

    BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);

    @SneakyThrows
    @Test
    void createBooking() {
        Mockito.when(bookingService.createBooking(bookingDto, 1L))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsBytes(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(booking.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.owner.id", is(booking.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.item.owner.name", is(booking.getItem().getOwner().getName())))
                .andExpect(jsonPath("$.item.owner.email", is(booking.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$.start", is(now.plusHours(1L).toString())))
                .andExpect(jsonPath("$.end", is(now.plusDays(1L).toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1)).createBooking(bookingDto, 1L);
    }

   @SneakyThrows
    @Test
    void patchBookingWithUpdatedStatus() {
        Booking updatedBooking = Booking.builder()
                        .id(booking.getId())
                                .item(booking.getItem())
                                        .booker(booking.getBooker())
                                                .start(booking.getStart())
                                                        .end(booking.getEnd())
                                                                .status(BookingStatus.APPROVED)
                                                                        .build();

       Mockito.when(bookingService.patchBookingWithUpdatedStatus(1L, 1L, true))
               .thenReturn(booking);
    }

    /* @SneakyThrows
    @Test
    void getBooking() {
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUser() {
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUserItems() {
    }*/
}