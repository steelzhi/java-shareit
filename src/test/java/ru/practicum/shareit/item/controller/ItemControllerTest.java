package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

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
            .name("Бензопила")
            .description("Makita")
            .available(false)
            .owner(user1)
            .build();

    List<Item> items = List.of(item1, item2);

    ItemDto itemDto1 = ItemDto.builder()
            .id(item1.getId())
            .name(item1.getName())
            .description(item1.getDescription())
            .owner(item1.getOwner())
            .requestId(item1.getRequestId())
            .build();

    ItemDto itemDto3 = ItemDto.builder()
            .id(item3.getId())
            .name(item3.getName())
            .description(item3.getDescription())
            .owner(item3.getOwner())
            .requestId(item3.getRequestId())
            .build();

    Booking booking1 = Booking.builder()
            .id(1L)
            .item(item1)
            .booker(user2)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusDays(1L))
            .status(BookingStatus.WAITING)
            .build();

    @SneakyThrows
    @Test
    void postItem() {
        Mockito.when(itemService.postItem(item1, user1.getId()))
                .thenReturn(item1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsBytes(item1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1)).postItem(item1, user1.getId());
    }

    @SneakyThrows
    @Test
    void patchItem() {
        Item patchedItem = Item.builder()
                .id(2L)
                .name(item2.getName())
                .description("Для воды, V = 10 л")
                .available(false)
                .owner(item2.getOwner())
                .build();

        Mockito.when(itemService.patchItem(2L, patchedItem, 2L))
                .thenReturn(patchedItem);

        mockMvc.perform(patch("/items/{itemId}", 2L)
                        .header("X-Sharer-User-Id", 2L)
                        .content(objectMapper.writeValueAsBytes(patchedItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(patchedItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item2.getName())))
                .andExpect(jsonPath("$.description", is(patchedItem.getDescription())))
                .andExpect(jsonPath("$.available", is(patchedItem.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getItemDtoById() {

        Mockito.when(itemService.getItemDtoById(1L, 1L))
                .thenReturn(itemDto1);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId())));

        Mockito.verify(itemService, Mockito.times(1)).getItemDtoById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getItemDtoByWrongId() {
        Mockito.when(itemService.getItemDtoById(100L, 1L))
                .thenThrow(new ItemDoesNotExistException("Вещи с таким id не существует"));

        mockMvc.perform(get("/items/100")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());

        Mockito.verify(itemService, Mockito.times(1)).getItemDtoById(100L, 1L);
    }

    @SneakyThrows
    @Test
    void getAllItemsDtoByUser() {
        long userId = 1L;
        Mockito.when(itemService.getAllItemsDtoByUser(userId, 1, 5))
                .thenReturn(List.of(itemDto1, itemDto3));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto1.getRequestId())))
                .andExpect(jsonPath("$[1].id", is(itemDto3.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto3.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto3.getDescription())))
                .andExpect(jsonPath("$[1].requestId", is(itemDto3.getRequestId())));

        Mockito.verify(itemService, Mockito.times(1)).getAllItemsDtoByUser(userId, 1, 5);
    }

    @SneakyThrows
    @Test
    void searchItems() {
        Mockito.when(itemService.searchItems("нист", null, null))
                .thenReturn(List.of(item2));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "нист"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item2.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item2.getName())))
                .andExpect(jsonPath("$[0].description", is(item2.getDescription())))
                .andExpect(jsonPath("$[0].requestId", is(item2.getRequestId())));

        Mockito.verify(itemService, Mockito.times(1)).searchItems("нист", null, null);
    }

/*@SneakyThrows
    @Test
    void postComment() {
    }*/
}