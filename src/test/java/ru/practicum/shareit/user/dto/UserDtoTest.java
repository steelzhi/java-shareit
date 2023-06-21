package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    JacksonTester<UserDto> json;

    User user = User.builder()
            .id(1L)
            .name("a")
            .email("aa")
            .build();

    UserDto userDto = UserMapper.mapToUserDto(user);

    @Test
    @SneakyThrows
    void testItemDto() {
        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(user.getId()));
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(user.getEmail());
    }
}