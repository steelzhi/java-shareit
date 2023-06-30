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
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForController;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.BookingDoesNotExistException;
import ru.practicum.shareit.exception.DuplicateStatusException;
import ru.practicum.shareit.exception.IllegalBookingAttemptException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    Item item1 = Item.builder()
            .id(1L)
            .name("УШМ")
            .description("Углошлифовальная машина")
            .available(true)
            .owner(user1)
            .build();

    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    BookingDtoRequest bookingDtoIn1 = BookingDtoRequest.builder()
            .id(1L)
            .start(now.plusHours(1L))
            .end(now.plusDays(1L))
            .itemId(1L)
            .bookerId(2L)
            .status(BookingStatus.WAITING)
            .build();

    Booking booking1 = BookingMapper.mapToBooking(bookingDtoIn1, item1, user2);

    BookingDtoResponseForController bookingDtoOutForController1 = BookingMapper.mapToBookingDtoOutForController(booking1);

    @SneakyThrows
    @Test
    void createBooking() {
        Mockito.when(bookingService.createBookingDto(bookingDtoIn1, 1L))
                .thenReturn(bookingDtoOutForController1);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsBytes(bookingDtoIn1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(booking1.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(booking1.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.owner.id", is(booking1.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.item.owner.name", is(booking1.getItem().getOwner().getName())))
                .andExpect(jsonPath("$.item.owner.email", is(booking1.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$.start", is(now.plusHours(1L).toString())))
                .andExpect(jsonPath("$.end", is(now.plusDays(1L).toString())))
                .andExpect(jsonPath("$.status", is(booking1.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1)).createBookingDto(bookingDtoIn1, 1L);
    }

    @SneakyThrows
    @Test
    void createBookingByItemOwner() {
        Mockito.when(bookingService.createBookingDto(bookingDtoIn1, 2L))
                .thenThrow(new IllegalBookingAttemptException("Владелец вещи не может бронировать свою вещь"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(objectMapper.writeValueAsBytes(bookingDtoIn1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).createBookingDto(bookingDtoIn1, 2L);
    }

    @SneakyThrows
    @Test
    void patchBookingWithUpdatedStatus() {
        BookingDtoRequest updatedBookingDtoIn = BookingDtoRequest.builder()
                .id(booking1.getId())
                .itemId(booking1.getItem().getId())
                .bookerId(booking1.getBooker().getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        Booking updatedBooking =
                BookingMapper.mapToBooking(updatedBookingDtoIn, booking1.getItem(), booking1.getBooker());

        BookingDtoResponseForController bookingDtoOutForController =
                BookingMapper.mapToBookingDtoOutForController(updatedBooking);

        Mockito.when(bookingService.patchBookingDtoWithUpdatedStatus(1L, 1L, true))
                .thenReturn(bookingDtoOutForController);

        mockMvc.perform(patch("/bookings/{bookingId}", booking1.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsBytes(updatedBookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(booking1.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(booking1.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.owner.id", is(booking1.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.item.owner.name", is(booking1.getItem().getOwner().getName())))
                .andExpect(jsonPath("$.item.owner.email", is(booking1.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$.start", is(now.plusHours(1L).toString())))
                .andExpect(jsonPath("$.end", is(now.plusDays(1L).toString())))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .patchBookingDtoWithUpdatedStatus(1L, 1L, true);
    }

    @SneakyThrows
    @Test
    void patchBookingWithDuplicateStatus() {
        booking1.setStatus(BookingStatus.APPROVED);

        BookingDtoRequest updatedBookingDtoIn = BookingDtoRequest.builder()
                .id(booking1.getId())
                .itemId(booking1.getItem().getId())
                .bookerId(booking1.getBooker().getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        Booking updatedBooking =
                BookingMapper.mapToBooking(updatedBookingDtoIn, booking1.getItem(), booking1.getBooker());

        BookingDtoResponseForController bookingDtoOutForController =
                BookingMapper.mapToBookingDtoOutForController(updatedBooking);

        Mockito.when(bookingService.patchBookingDtoWithUpdatedStatus(1L, 1L, true))
                .thenReturn(bookingDtoOutForController);

        Mockito.when(bookingService.patchBookingDtoWithUpdatedStatus(1L, 1L, true))
                .thenThrow(new DuplicateStatusException("Данный статус уже установлен"));

        mockMvc.perform(patch("/bookings/{bookingId}", booking1.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsBytes(updatedBookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.times(1))
                .patchBookingDtoWithUpdatedStatus(1L, 1L, true);
    }

    @SneakyThrows
    @Test
    void getBooking() {
        Mockito.when(bookingService.getBookingDto(1L, 2L))
                .thenReturn(bookingDtoOutForController1);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(booking1.getItem().getDescription())))
                .andExpect(jsonPath("$.item.available", is(booking1.getItem().getAvailable())))
                .andExpect(jsonPath("$.item.owner.id", is(booking1.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.item.owner.name", is(booking1.getItem().getOwner().getName())))
                .andExpect(jsonPath("$.item.owner.email", is(booking1.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$.start", is(now.plusHours(1L).toString())))
                .andExpect(jsonPath("$.end", is(now.plusDays(1L).toString())))
                .andExpect(jsonPath("$.status", is(booking1.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1)).getBookingDto(1L, 2L);
    }

    @SneakyThrows
    @Test
    void getNotExistingBooking() {
        Mockito.when(bookingService.getBookingDto(100L, 2L))
                .thenThrow(new BookingDoesNotExistException("Бронирование не найдено"));

        mockMvc.perform(get("/bookings/100")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).getBookingDto(100L, 2L);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUser() {
        BookingDtoRequest bookingDtoIn2 = BookingDtoRequest.builder()
                .id(2L)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING)
                .build();

        Booking booking2 = BookingMapper.mapToBooking(bookingDtoIn2, item1, user2);

        BookingDtoResponseForController bookingDtoOutForController2 =
                BookingMapper.mapToBookingDtoOutForController(booking2);
        Mockito.when(bookingService.getAllBookingDtosByUser(2L, BookingStatus.WAITING, null, null))
                .thenReturn(List.of(bookingDtoOutForController1, bookingDtoOutForController2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking1.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(booking1.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(booking1.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.owner.id",
                        is(booking1.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.owner.name", is(booking1.getItem().getOwner().getName())))
                .andExpect(jsonPath("$[0].item.owner.email", is(booking1.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$[0].start", is(now.plusHours(1L).toString())))
                .andExpect(jsonPath("$[0].end", is(now.plusDays(1L).toString())))
                .andExpect(jsonPath("$[0].status", is(booking1.getStatus().name())))
                .andExpect(jsonPath("$[1].id", is(booking2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(booking2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(booking2.getItem().getName())))
                .andExpect(jsonPath("$[1].item.description", is(booking2.getItem().getDescription())))
                .andExpect(jsonPath("$[1].item.available", is(booking2.getItem().getAvailable())))
                .andExpect(jsonPath("$[1].item.owner.id",
                        is(booking2.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.owner.name", is(booking2.getItem().getOwner().getName())))
                .andExpect(jsonPath("$[1].item.owner.email", is(booking2.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$[1].start", is(now.plusDays(2L).toString())))
                .andExpect(jsonPath("$[1].end", is(now.plusDays(3L).toString())))
                .andExpect(jsonPath("$[1].status", is(booking2.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingDtosByUser(2L, BookingStatus.WAITING, null, null);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUserWithPaginationAndNotEmptyResult() {
        BookingDtoRequest bookingDtoIn2 = BookingDtoRequest.builder()
                .id(2L)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING)
                .build();

        Booking booking2 = BookingMapper.mapToBooking(bookingDtoIn2, item1, user2);

        BookingDtoResponseForController bookingDtoOutForController2 =
                BookingMapper.mapToBookingDtoOutForController(booking2);
        Mockito.when(bookingService.getAllBookingDtosByUser(2L, BookingStatus.WAITING, 0, 5))
                .thenReturn(List.of(bookingDtoOutForController1, bookingDtoOutForController2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking1.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(booking1.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(booking1.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.owner.id",
                        is(booking1.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.owner.name", is(booking1.getItem().getOwner().getName())))
                .andExpect(jsonPath("$[0].item.owner.email", is(booking1.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$[0].start", is(now.plusHours(1L).toString())))
                .andExpect(jsonPath("$[0].end", is(now.plusDays(1L).toString())))
                .andExpect(jsonPath("$[0].status", is(booking1.getStatus().name())))
                .andExpect(jsonPath("$[1].id", is(booking2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(booking2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(booking2.getItem().getName())))
                .andExpect(jsonPath("$[1].item.description", is(booking2.getItem().getDescription())))
                .andExpect(jsonPath("$[1].item.available", is(booking2.getItem().getAvailable())))
                .andExpect(jsonPath("$[1].item.owner.id",
                        is(booking2.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.owner.name", is(booking2.getItem().getOwner().getName())))
                .andExpect(jsonPath("$[1].item.owner.email", is(booking2.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$[1].start", is(now.plusDays(2L).toString())))
                .andExpect(jsonPath("$[1].end", is(now.plusDays(3L).toString())))
                .andExpect(jsonPath("$[1].status", is(booking2.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingDtosByUser(2L, BookingStatus.WAITING, 0, 5);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUserWithPaginationAndEmptyResult() {
        BookingDtoRequest bookingDtoIn2 = BookingDtoRequest.builder()
                .id(2L)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING)
                .build();

        Booking booking2 = BookingMapper.mapToBooking(bookingDtoIn2, item1, user2);

        BookingDtoResponseForController bookingDtoOutForController2 =
                BookingMapper.mapToBookingDtoOutForController(booking2);
        Mockito.when(bookingService.getAllBookingDtosByUser(2L, BookingStatus.WAITING, 10, 50))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "WAITING")
                        .param("from", "10")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", is(new ArrayList())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingDtosByUser(2L, BookingStatus.WAITING, 10, 50);
    }

/*    @SneakyThrows
    @Test
    void getAllBookingsByUserWithUnsupportedStatus() {
        Mockito.when(bookingService.getAllBookingDtosByUser(2L, "UNSUPPORTED", null, null))
                .thenThrow(new WrongBookingStatusException("Unknown state: UNSUPPORTED_STATUS"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "UNSUPPORTED"))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingDtosByUser(2L, "UNSUPPORTED", null, null);
    }*/

    @SneakyThrows
    @Test
    void getAllBookingsForUserItems() {
        BookingDtoRequest bookingDtoIn2 = BookingDtoRequest.builder()
                .id(2L)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING)
                .build();

        Booking booking2 = BookingMapper.mapToBooking(bookingDtoIn2, item1, user2);

        BookingDtoResponseForController bookingDtoOutForController2 =
                BookingMapper.mapToBookingDtoOutForController(booking2);

        Mockito.when(bookingService.getAllBookingDtosForUserItems(1L, BookingStatus.WAITING, 0, 5))
                .thenReturn(List.of(bookingDtoOutForController1, bookingDtoOutForController2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking1.getItem().getName())))
                .andExpect(jsonPath("$[0].item.description", is(booking1.getItem().getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(booking1.getItem().getAvailable())))
                .andExpect(jsonPath("$[0].item.owner.id",
                        is(booking1.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.owner.name", is(booking1.getItem().getOwner().getName())))
                .andExpect(jsonPath("$[0].item.owner.email", is(booking1.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$[0].start", is(now.plusHours(1L).toString())))
                .andExpect(jsonPath("$[0].end", is(now.plusDays(1L).toString())))
                .andExpect(jsonPath("$[0].status", is(booking1.getStatus().name())))
                .andExpect(jsonPath("$[1].id", is(booking2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(booking2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(booking2.getItem().getName())))
                .andExpect(jsonPath("$[1].item.description", is(booking2.getItem().getDescription())))
                .andExpect(jsonPath("$[1].item.available", is(booking2.getItem().getAvailable())))
                .andExpect(jsonPath("$[1].item.owner.id",
                        is(booking2.getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.owner.name", is(booking2.getItem().getOwner().getName())))
                .andExpect(jsonPath("$[1].item.owner.email", is(booking2.getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$[1].start", is(now.plusDays(2L).toString())))
                .andExpect(jsonPath("$[1].end", is(now.plusDays(3L).toString())))
                .andExpect(jsonPath("$[1].status", is(booking2.getStatus().name())));
    }
}
