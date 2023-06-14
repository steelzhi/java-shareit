package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SqlGroup({
        @Sql(scripts = "classpath:schema.sql",
                config = @SqlConfig(encoding = "UTF-8"),
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:create_test_data.sql",
                config = @SqlConfig(encoding = "UTF-8"),
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserJpaRepositoryTests {

    @Autowired
    UserRepository userRepository;

    @Test
    void getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        assertEquals(allUsers.size(), 3, "Некорректное количество пользователей в БД");
    }

    @Test
    void get2ndUser() {
        User user2 = userRepository.getReferenceById(2L);
        assertEquals(user2.getName(), "user2", "Некорректное имя пользователя с id = 2");
        assertEquals(user2.getEmail(), "user2@email.com", "Некорректный email пользователя с id = 2");
    }

    @Test
    void createUser() {
        User user = new User(null, "user4", "user4@email.com");
        User createdUser = userRepository.save(user);
        assertTrue(userRepository.findAll().size() == 4,
                "После добавление пользователя размер списка пользователей неправильный");
        assertEquals(user.getName(), createdUser.getName(), "Имена не совпадают");
    }

    @Test
    void deleteUsers() {
        userRepository.deleteAllInBatch();
        assertTrue(userRepository.findAll().isEmpty(),
                "После удаления всех пользователей список пользователей в БД не пуст");
    }
}