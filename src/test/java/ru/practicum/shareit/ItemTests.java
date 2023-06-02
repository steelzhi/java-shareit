package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemDtoRepository;
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
    ItemDtoRepository itemDtoRepository;
    ItemService itemService = new ItemServiceImpl(itemDtoRepository, userRepository);
    ItemController itemController = new ItemController(itemService);

    static Validator validator;

    @BeforeAll
    static void createValidator() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateCorrectItemDto() {
        ItemDto itemDto1 = ItemDto.builder()
                .name("Дрель")
                .description("Дрель-шуруповерт электрический")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violationSet = validator.validate(itemDto1);
        assertTrue(violationSet.isEmpty(),
                "При добавлении предмета с корректными параметрами происходит ошибка");
    }

    @Test
    void validateItemDtoWithEmptyName() {
        ItemDto itemDto1 = ItemDto.builder()
                .description("Дрель-шуруповерт электрический")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violationSet = validator.validate(itemDto1);
        assertTrue(violationSet.size() == 1,
                "При добавлении предмета без названия не происходит ошибки");
    }

    @Test
    void validateItemDtoWithEmptyDescription() {
        ItemDto itemDto1 = ItemDto.builder()
                .name("Дрель")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violationSet = validator.validate(itemDto1);
        assertTrue(violationSet.size() == 1,
                "При добавлении предмета без описания не происходит ошибки");
    }

    @Test
    void validateItemDtoWithEmptyAvailableStatus() {
        ItemDto itemDto1 = ItemDto.builder()
                .name("Дрель")
                .description("Дрель-шуруповерт электрический")
                .build();

        Set<ConstraintViolation<ItemDto>> violationSet = validator.validate(itemDto1);
        assertTrue(violationSet.size() == 1,
                "При добавлении предмета без статуса занятости не происходит ошибки");
    }

    @Test
    void getAllItemsDto() {
        User user1 = new User(null, "User1", "user1@email.com");
        Long user1Id = userService.postUser(user1).getId();

        ItemDto itemDto1 = new ItemDto(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);
        ItemDto itemDto2 = new ItemDto(null, "УШМ", "Углошлифовальная машина с плавным пуском",
                false, null, null);
        ItemDto itemDto1FromList = itemController.postItemDto(itemDto1, user1Id);
        ItemDto itemDto2FromList = itemController.postItemDto(itemDto2, user1Id);

        assertTrue(itemController.findAll(user1Id).size() == 2,
                "Количество добавленных пользователем с id = " + user1Id + " в списке не совпадает с " +
                        "фактическим количеством добавленных этим пользователем вещей.");
        assertEquals(itemController.findAll(user1Id), List.of(itemDto1FromList, itemDto2FromList), "" +
                "Список добавленных пользователем с id = " + user1Id + " вещей не совпадает с " +
                "фактически добавленными этим пользователем вещами.");
    }

    @Test
    void getAllItemsDtoByNotExistingUser() {
        Long user1Id = 10L;

        ItemDto itemDto1 = new ItemDto(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);

        UserDoesNotExistException userDoesNotExistException = assertThrows(UserDoesNotExistException.class,
                () -> itemController.postItemDto(itemDto1, user1Id));

        assertEquals("Пользователя с указанным id не существует.",
                userDoesNotExistException.getMessage(),
                "Из списка получены вещи, принадлежащие пользователю с несуществующим id = " + user1Id);
    }

    @Test
    void postAndGetItemDto() {
        User user1 = new User(null, "User1", "user1@email.com");
        Long user1Id = userService.postUser(user1).getId();

        ItemDto itemDto1 = new ItemDto(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);
        ItemDto itemDto2 = new ItemDto(null, "УШМ", "Углошлифовальная машина с плавным пуском",
                false, null, null);
        ItemDto itemDto1FromList = itemController.postItemDto(itemDto1, user1Id);
        ItemDto itemDto2FromList = itemController.postItemDto(itemDto2, user1Id);

        assertEquals(itemController.getItemDto(itemDto2FromList.getId()), itemDto2,
                "Добавленная в список и полученная из списка вещь не совпадают");
    }

    @Test
    void patchItemDto() {
        User user1 = new User(null, "User1", "user1@email.com");
        Long user1Id = userService.postUser(user1).getId();

        ItemDto itemDto1 = new ItemDto(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);
        ItemDto itemDto1FromList = itemController.postItemDto(itemDto1, user1Id);

        ItemDto updatedItemDto1 = new ItemDto(itemDto1FromList.getId(), "Дрель-шуруповерт",
                "Шуруповерт электрический DeWalt",
                false, null, null);
        itemController.patchItemDto(itemDto1FromList.getId(), updatedItemDto1, user1Id);

        assertTrue(itemController.findAll(user1Id).size() == 1,
                "После изменения количество вещей изменилось");
        assertEquals(itemController.getItemDto(itemDto1FromList.getId()), updatedItemDto1,
                "Обновленная вещь в списке не совпадает с фактически обновленной вещью");
    }

    @Test
    void searchItemDto() {
        User user1 = new User(null, "User1", "user1@email.com");
        Long user1Id = userService.postUser(user1).getId();

        ItemDto itemDto1 = new ItemDto(null, "Дрель", "Дрель-шуруповерт электрический",
                true, null, null);
        ItemDto itemDto2 = new ItemDto(null, "УШМ",
                "Углошлифовальная Makita машина с плавным пуском", false, null, null);
        ItemDto itemDto3 = new ItemDto(null, "УШМ",
                "Углошлифовальная DeWalt машина с плавным пуском", true, null, null);
        itemController.postItemDto(itemDto1, user1Id);
        itemController.postItemDto(itemDto2, user1Id);
        itemController.postItemDto(itemDto3, user1Id);


        List<ItemDto> itemsDtoList1 = itemController.searchItems("рель");
        List<ItemDto> itemsDtoList2 = itemController.searchItems("шлифова");
        List<ItemDto> itemsDtoList3 = itemController.searchItems("ш");

        assertEquals(itemsDtoList1, List.of(itemDto1), "Поиск не находит 1-ю вещь.");
        assertFalse(itemsDtoList2.contains(itemDto2), "Поиск находит 2-ю вещь, хотя она не доступна");
        assertEquals(itemsDtoList2, List.of(itemDto3), "Поиск не находит 3-ю вещь.");
        assertEquals(itemsDtoList3, List.of(itemDto1, itemDto3), "Поиск не находит 1-ю и 3-ю вещи.");
    }
}