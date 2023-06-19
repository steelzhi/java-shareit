package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    JacksonTester<ItemRequestDto> json;

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


    LocalDateTime now = LocalDateTime.now();

    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user2)
            .description("Хотел бы воспользоваться УШМ")
            .created(now)
            .build();

    ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, List.of(itemDtoForSearch1));

    @Test
    @SneakyThrows
    void testItemDto() {
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(itemRequest.getId()));
        assertThat(result).extractingJsonPathNumberValue("$.requesterDto.id")
                .isEqualTo(Math.toIntExact(itemRequest.getRequester().getId()));
        assertThat(result).extractingJsonPathStringValue("$.requesterDto.name")
                .isEqualTo(itemRequest.getRequester().getName());
        assertThat(result).extractingJsonPathStringValue("$.requesterDto.email")
                .isEqualTo(itemRequest.getRequester().getEmail());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequest.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
    }
}
