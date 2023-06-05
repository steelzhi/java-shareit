package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTests {
    //UserRepository userRepository = new InMemoryUserRepository();
    UserRepository userRepository;
    UserService userService = new UserServiceImpl(userRepository);

    //ItemDtoRepository itemDtoRepository = new InMemoryItemDtoRepository();
    ItemRepository itemDtoRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemService itemService = new ItemServiceImpl(itemDtoRepository, userRepository, bookingRepository, commentRepository);
    ItemController itemController = new ItemController(itemService);

    static Validator validator;

    @BeforeAll
    static void createValidator() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateCorrectItemDto() {
        Item itemDto1 = Item.builder()
                .name("Дрель")
                .description("Дрель-шуруповерт электрический")
                .available(true)
                .build();

        Set<ConstraintViolation<Item>> violationSet = validator.validate(itemDto1);
        assertTrue(violationSet.isEmpty(),
                "При добавлении предмета с корректными параметрами происходит ошибка");
    }

    @Test
    void validateItemDtoWithEmptyName() {
        Item itemDto1 = Item.builder()
                .description("Дрель-шуруповерт электрический")
                .available(true)
                .build();

        Set<ConstraintViolation<Item>> violationSet = validator.validate(itemDto1);
        assertTrue(violationSet.size() == 1,
                "При добавлении предмета без названия не происходит ошибки");
    }

    @Test
    void validateItemDtoWithEmptyDescription() {
        Item itemDto1 = Item.builder()
                .name("Дрель")
                .available(true)
                .build();

        Set<ConstraintViolation<Item>> violationSet = validator.validate(itemDto1);
        assertTrue(violationSet.size() == 1,
                "При добавлении предмета без описания не происходит ошибки");
    }

    @Test
    void validateItemDtoWithEmptyAvailableStatus() {
        Item itemDto1 = Item.builder()
                .name("Дрель")
                .description("Дрель-шуруповерт электрический")
                .build();

        Set<ConstraintViolation<Item>> violationSet = validator.validate(itemDto1);
        assertTrue(violationSet.size() == 1,
                "При добавлении предмета без статуса занятости не происходит ошибки");
    }

    @Test
    void getAllItemsDto() {
        User user1 = new User(null, "User1", "user1@email.com");
        Long user1Id = userService.postUser(user1).getId();

        Item itemDto1 = new Item(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);
        Item itemDto2 = new Item(null, "УШМ", "Углошлифовальная машина с плавным пуском",
                false, null, null);
        Item itemDto1FromList = itemController.postItem(itemDto1, user1Id);
        Item itemDto2FromList = itemController.postItem(itemDto2, user1Id);

        assertTrue(itemController.getAllItemsDtoByUser(user1Id).size() == 2,
                "Количество добавленных пользователем с id = " + user1Id + " в списке не совпадает с " +
                        "фактическим количеством добавленных этим пользователем вещей.");
        assertEquals(itemController.getAllItemsDtoByUser(user1Id), List.of(itemDto1FromList, itemDto2FromList), "" +
                "Список добавленных пользователем с id = " + user1Id + " вещей не совпадает с " +
                "фактически добавленными этим пользователем вещами.");
    }

    @Test
    void getAllItemsDtoByNotExistingUser() {
        Long user1Id = 10L;

        Item itemDto1 = new Item(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);

        UserDoesNotExistException userDoesNotExistException = assertThrows(UserDoesNotExistException.class,
                () -> itemController.postItem(itemDto1, user1Id));

        assertEquals("Пользователя с указанным id не существует.",
                userDoesNotExistException.getMessage(),
                "Из списка получены вещи, принадлежащие пользователю с несуществующим id = " + user1Id);
    }

    @Test
    void postAndGetItemDto() {
        User user1 = new User(null, "User1", "user1@email.com");
        Long user1Id = userService.postUser(user1).getId();

        Item itemDto1 = new Item(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);
        Item itemDto2 = new Item(null, "УШМ", "Углошлифовальная машина с плавным пуском",
                false, null, null);
        Item itemDto1FromList = itemController.postItem(itemDto1, user1Id);
        Item itemDto2FromList = itemController.postItem(itemDto2, user1Id);

        assertEquals(itemController.getItemDtoById(itemDto2FromList.getId(), null), itemDto2,
                "Добавленная в список и полученная из списка вещь не совпадают");
    }

    @Test
    void patchItemDto() {
        User user1 = new User(null, "User1", "user1@email.com");
        Long user1Id = userService.postUser(user1).getId();

        Item itemDto1 = new Item(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);
        Item itemDto1FromList = itemController.postItem(itemDto1, user1Id);

        Item updatedItemDto1 = new Item(itemDto1FromList.getId(), "Дрель-шуруповерт",
                "Шуруповерт электрический DeWalt",
                false, null, null);
        itemController.patchItem(itemDto1FromList.getId(), updatedItemDto1, user1Id);

        assertTrue(itemController.getAllItemsDtoByUser(user1Id).size() == 1,
                "После изменения количество вещей изменилось");
        assertEquals(itemController.getItemDtoById(itemDto1FromList.getId(), null), updatedItemDto1,
                "Обновленная вещь в списке не совпадает с фактически обновленной вещью");
    }

    @Test
    void searchItemDto() {
        User user1 = new User(null, "User1", "user1@email.com");
        Long user1Id = userService.postUser(user1).getId();

        Item itemDto1 = new Item(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);
        Item itemDto2 = new Item(null, "УШМ",
                "Углошлифовальная Makita машина с плавным пуском", false, null, null);
        Item itemDto3 = new Item(null, "УШМ",
                "Углошлифовальная DeWalt машина с плавным пуском", true, null, null);
        itemController.postItem(itemDto1, user1Id);
        itemController.postItem(itemDto2, user1Id);
        itemController.postItem(itemDto3, user1Id);


        List<Item> itemsDtoList1 = itemController.searchItems("рель");
        List<Item> itemsDtoList2 = itemController.searchItems("шлифова");
        List<Item> itemsDtoList3 = itemController.searchItems("ш");

        assertEquals(itemsDtoList1, List.of(itemDto1), "Поиск не находит 1-ю вещь.");
        assertFalse(itemsDtoList2.contains(itemDto2), "Поиск находит 2-ю вещь, хотя она не доступна");
        assertEquals(itemsDtoList2, List.of(itemDto3), "Поиск не находит 3-ю вещь.");
        assertEquals(itemsDtoList3, List.of(itemDto1, itemDto3), "Поиск не находит 1-ю и 3-ю вещи.");
    }
}