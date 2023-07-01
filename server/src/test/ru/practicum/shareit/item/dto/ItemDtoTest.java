package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    JacksonTester<ItemDto> json;

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@user.ru")
            .build();

    Item item1 = Item.builder()
            .id(1L)
            .name("УШМ")
            .description("Углошлифовальная машина")
            .available(true)
            .owner(user1)
            .build();

    ItemDto itemDto1 = ItemMapper.mapToItemDto(item1, null, null, new ArrayList<>());

    @Test
    @SneakyThrows
    void testItemDto() {
        JsonContent<ItemDto> result = json.write(itemDto1);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(item1.getId()));
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item1.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(item1.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id")
                .isEqualTo(Math.toIntExact(user1.getId()));
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo(user1.getName());
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo(user1.getEmail());
    }
}
