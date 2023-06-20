package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmptyDescriptionException;
import ru.practicum.shareit.exception.RequestDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestService itemRequestService;

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

    ItemDtoForSearch itemDtoForSearch1 = ItemMapper.mapToItemDtoForSearch(item1);

    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user2)
            .description("Хотел бы воспользоваться УШМ")
            .created(now)
            .build();

    ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, List.of(itemDtoForSearch1));

    @SneakyThrows
    @Test
    void postItemRequest() {
        ItemRequest itemRequestOnlyWithDescription = ItemRequest.builder()
                .id(1L)
                .description("Хотел бы воспользоваться УШМ")
                .build();
        ItemRequestDto itemRequestDtoOnlyWithDescription = ItemRequestMapper.mapToItemRequestDto(itemRequestOnlyWithDescription, new ArrayList<>());
        Mockito.when(itemRequestService.postItemRequestDto(2L, itemRequestDtoOnlyWithDescription))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .content(objectMapper.writeValueAsBytes(itemRequestDtoOnlyWithDescription))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.requesterDto.id", is(itemRequest.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requesterDto.name", is(itemRequest.getRequester().getName())))
                .andExpect(jsonPath("$.requesterDto.email", is(itemRequest.getRequester().getEmail())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(now.toString())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .postItemRequestDto(2L, itemRequestDtoOnlyWithDescription);
    }

    @SneakyThrows
    @Test
    void postItemRequestWithEmptyDescription() {
        ItemRequest itemRequestWithoutDescription = ItemRequest.builder()
                .id(1L)
                .build();
        ItemRequestDto itemRequestDtoWithoutDescription = ItemRequestMapper.mapToItemRequestDto(itemRequestWithoutDescription, new ArrayList<>());
        Mockito.when(itemRequestService.postItemRequestDto(2L, itemRequestDtoWithoutDescription))
                .thenThrow(new EmptyDescriptionException("Описание запроса не может быть пустым"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .content(objectMapper.writeValueAsBytes(itemRequestDtoWithoutDescription))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemRequestService, Mockito.times(1))
                .postItemRequestDto(2L, itemRequestDtoWithoutDescription);
    }

    @SneakyThrows
    @Test
    void getAllRequestsMadeByRequester() {
        Mockito.when(itemRequestService.getAllRequestDtosMadeByRequester(2L))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].requesterDto.id", is(itemRequestDto.getRequesterDto().getId()), Long.class))
                .andExpect(jsonPath("$[0].requesterDto.name", is(itemRequestDto.getRequesterDto().getName())))
                .andExpect(jsonPath("$[0].requesterDto.email", is(itemRequestDto.getRequesterDto().getEmail())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(now.toString())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllRequestDtosMadeByRequester(2L);
    }

    @SneakyThrows
    @Test
    void getPagedRequestsMadeByOtherUsers() {
        Mockito.when(itemRequestService.getPagedRequestDtosMadeByOtherUsers(1L, null, null))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", is(new ArrayList())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getPagedRequestDtosMadeByOtherUsers(1L, null, null);
    }

    @SneakyThrows
    @Test
    void getRequestDto() {
        Mockito.when(itemRequestService.getRequestDto(2L, 1L))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.requesterDto.id", is(itemRequest.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requesterDto.name", is(itemRequest.getRequester().getName())))
                .andExpect(jsonPath("$.requesterDto.email", is(itemRequest.getRequester().getEmail())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(now.toString())));

        Mockito.verify(itemRequestService, Mockito.times(1)).getRequestDto(2L, 1L);
    }

    @SneakyThrows
    @Test
    void getNotExistingRequestDto() {
        Mockito.when(itemRequestService.getRequestDto(2L, 100L))
                .thenThrow(new RequestDoesNotExistException("Запрос не найден"));

        mockMvc.perform(get("/requests/100")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isNotFound());

        Mockito.verify(itemRequestService, Mockito.times(1)).getRequestDto(2L, 100L);
    }
}