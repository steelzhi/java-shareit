package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoForSearchTest {

    @Autowired
    JacksonTester<ItemDtoForSearch> json;

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

    ItemDtoForSearch itemDtoForSearch1 = ItemMapper.mapToItemDtoForSearch(item1);

    @Test
    @SneakyThrows
    void testItemDtoForSearch() {
        JsonContent<ItemDtoForSearch> result = json.write(itemDtoForSearch1);

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