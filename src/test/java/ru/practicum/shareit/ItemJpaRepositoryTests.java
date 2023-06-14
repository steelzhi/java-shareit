package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
public class ItemJpaRepositoryTests {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void getAllItems() {
        List<Item> allItems = itemRepository.findAll();
        assertEquals(allItems.size(), 3, "Некорректное количество предметов в БД");
    }

    @Test
    void get3rdItem() {
        Item item3 = itemRepository.getReferenceById(3L);

        assertEquals(item3.getName(), "Клей Момент", "Некорректное имя предмета с id = 3");
        assertEquals(item3.getDescription(), "Тюбик суперклея марки Момент",
                "Некорректное описание предмета с id =3");
        assertEquals(item3.getOwner().getId(), 2, "Некорректное id владельца предмета с id = 3");
        assertTrue(item3.getAvailable(), "Некорректный статус занятость предмета с id = 3");
    }

    @Test
    void createItem() {
        User user1 = userRepository.getReferenceById(1L);
        Item item4 = new Item(
                null,
                "Бензопила",
                "Бензопила DeWalt",
                false,
                user1,
                null);
        Item item4Created = itemRepository.save(item4);
        assertTrue(itemRepository.findAll().size() == 4,
                "После добавления нового предмета размер списка предметов неправильный");
        assertEquals(item4.getName(), item4Created.getName(),
                "Названия добавленного предмета и предмета в БД не совпадают");
        assertEquals(item4.getOwner(), item4Created.getOwner(),
                "Владельцы добавленного предмета и предмета в БД не совпадают");
    }

    @Test
    void deleteItems() {
        itemRepository.deleteAllInBatch();
        assertTrue(itemRepository.findAll().isEmpty(),
                "После удаления всех предметов список предметов в БД не пуст");
    }

    @Test
    void findAllByOwnerId() {
        List<Item> allUser2Items = itemRepository.findAllByOwner_Id(2L);
        assertEquals(allUser2Items.size(), 1,
                "Некорректное количество предметов в БД у пользователя с id = 4");
        assertEquals(allUser2Items, List.of(itemRepository.getReferenceById(3L)));
    }

    @Test
    void searchAvailableItems() {
        List<Item> itemsWithMome = itemRepository.searchItems("мОмЕ");
        assertTrue(itemsWithMome.size() == 1, "Поиск выдал некорректное количество предметов");

        List<Item> itemsWithAkku = itemRepository.searchItems("АККУ");
        assertTrue(itemsWithAkku.size() == 1, "Поиск выдал некорректное количество предметов");
    }
}