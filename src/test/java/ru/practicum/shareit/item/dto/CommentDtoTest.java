package ru.practicum.shareit.item.dto;

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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    JacksonTester<CommentDto> json;

    @SneakyThrows
    @Test
    void testCommentDto() {
        User user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@user.ru")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("user2")
                .email("user2@user.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(true)
                .owner(user1)
                .build();

        LocalDateTime now = LocalDateTime.now();

        CommentDto commentDto = CommentDto.builder()
                .text("this is a comment")
                .item(item)
                .authorName(user2.getName())
                .created(now)
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("this is a comment");
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(Math.toIntExact(item.getId()));
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("item description");
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id")
                .isEqualTo(Math.toIntExact(user1.getId()));
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("user1@user.ru");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("user2");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(now.format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss")));
    }
}