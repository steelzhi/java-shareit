package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {
    UserRepository userRepository = new InMemoryUserRepository();
    UserService userService = new UserService(userRepository);
    UserController userController = new UserController(userService);
    static Validator validator;

    @BeforeAll
    static void createValidator() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validateCorrectUser() {
        User user = User.builder()
                .name("user")
                .email("user@email.com")
                .build();

        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        assertEquals(0, violationSet.size(),
                "При добавлении пользователя с корректными параметрами происходит ошибка");
    }

    @Test
    void validateUserWithEmptyName() {
        User user = User.builder()
                .email("user@email.com")
                .build();

        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        assertEquals(1, violationSet.size(),
                "При добавлении пользователя без имени не происходит ошибки");
    }

    @Test
    void validateUserWithIncorrectEmail() {
        User user = User.builder()
                .name("user")
                .email("useremail.com")
                .build();

        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        assertEquals(1, violationSet.size(),
                "При добавлении пользователя с некорректным email не происходит ошибки");
    }

    @Test
    void getUsers() {
        User user1 = new User(null, "User1", "user1@email.com");
        User user2 = new User(null, "User2", "user2@email.com");
        userController.postUser(user1);
        userController.postUser(user2);

        List<User> users = userController.getUsers();

        assertTrue(users.size() == 2,
                "Количество пользователей в списке не совпадает с количеством добавленных пользователей.");

        assertEquals(List.of(user1, user2), users,
                "Пользователи в списке не совпадают с добавленными пользователями");
    }

    @Test
    void postUserAndGetUser() {
        User user1 = new User(null, "User1", "user1@email.com");
        User user2 = new User(null, "User2", "user2@email.com");

        User user1FromList = userController.postUser(user1);
        User user2FromList = userController.postUser(user2);

        assertEquals(user1.getName(), user1FromList.getName(),
                "Имя пользователя " + user1 + " в списке не совпадает с реальным именем этого пользователя");
        assertEquals(user1.getEmail(), user1FromList.getEmail(),
                "Email пользователя " + user1 + " в списке не совпадает с реальным email этого пользователя");
    }

    @Test
    void patchUser() {
        User user1 = new User(null, "User1", "user1@email.com");
        User user1FromList = userController.postUser(user1);
        Long user1Id = user1FromList.getId();

        User updatedUser1 = new User(user1Id, "UpdatedUser1", "updateUser1@email.com");
        User updatedUser1FromList = userController.patchUser(user1Id, updatedUser1);

        assertTrue(userController.getUsers().size() == 1,
                "При изменении пользователя количество пользователей в списке стало некорректным");
        assertTrue(userController.getUsers().contains(updatedUser1FromList),
                "Список не содержит актуальные данные пользователя, который был изменен");
    }

    @Test
    void patchUserWithDuplicateEmail() {
        User user1 = new User(null, "User1", "user1@email.com");
        User user1FromList = userController.postUser(user1);
        Long user1Id = user1FromList.getId();

        User user2 = new User(null, "User2", "user2@email.com");
        userController.postUser(user2);

        User updatedUser1 = new User(user1Id, "UpdatedUser1", "user2@email.com");

        DuplicateEmailException duplicateEmailException = assertThrows(DuplicateEmailException.class,
                () -> userController.patchUser(user1Id, updatedUser1));

        assertEquals("Нельзя изменить email на указанный - пользователь с таким email уже существует.",
                duplicateEmailException.getMessage(),
                "Изменному пользователю присвоен email другого, уже существующего в списке пользователя");
    }

    @Test
    void deleteUser() {
        User user1 = new User(null, "User1", "user1@email.com");
        User user1FromList = userController.postUser(user1);
        Long user1Id = user1FromList.getId();

        userController.deleteUser(user1Id);
        assertTrue(userController.getUsers().isEmpty(), "После удаления пользователь остался в списке.");
    }

    @AfterEach
    void clearAllUsers() {
        userRepository.deleteAllUsers();
    }
}