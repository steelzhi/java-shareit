package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
public class CommentJpaRepositoryTests {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;

    Booking booking1;
    Booking booking2;
    Booking booking3;

    @BeforeEach
    void createBooking() {
        booking1 = new Booking(null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1L),
                itemRepository.getReferenceById(1L),
                userRepository.getReferenceById(1L),
                BookingStatus.WAITING);
        booking2 = new Booking(null,
                LocalDateTime.now().plusHours(5L),
                LocalDateTime.now().plusDays(2L),
                itemRepository.getReferenceById(2L),
                userRepository.getReferenceById(2L),
                null);
        booking3 = new Booking(null,
                LocalDateTime.now().plusHours(10L),
                LocalDateTime.now().plusDays(3L),
                itemRepository.getReferenceById(3L),
                userRepository.getReferenceById(3L),
                null);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
    }

    @Test
    void createComment() {
        Item item1 = itemRepository.getReferenceById(1L);
        User user1 = userRepository.getReferenceById(1L);
        Comment comment = new Comment(null, "this is a comment", item1, user1, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        assertEquals(comment.getText(), savedComment.getText(), "Некорректно сохранен текст комментария");
        assertFalse(commentRepository.findAll().isEmpty(), "Комментарий не добавлен в БД");
    }

    @Test
    void getAllBookings() {
        Item item1 = itemRepository.getReferenceById(1L);
        User user1 = userRepository.getReferenceById(1L);
        Comment comment = new Comment(null, "this is a comment", item1, user1, LocalDateTime.now());
        commentRepository.save(comment);

        Item item2 = itemRepository.getReferenceById(2L);
        User user2 = userRepository.getReferenceById(2L);
        Comment comment2 = new Comment(null, "this is a 2nd comment", item2, user2, LocalDateTime.now());
        commentRepository.save(comment2);

        assertTrue(commentRepository.findAll().size() == 2,
                "Некорректное количество комментариев в БД");
    }

    @Test
    void get2ndComment() {
        Item item1 = itemRepository.getReferenceById(1L);
        User user1 = userRepository.getReferenceById(1L);
        Comment comment = new Comment(null, "this is a comment", item1, user1, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        Item item2 = itemRepository.getReferenceById(2L);
        User user2 = userRepository.getReferenceById(2L);
        Comment comment2 = new Comment(null, "this is a 2nd comment", item2, user2, LocalDateTime.now());
        Comment savedComment2 = commentRepository.save(comment2);

        assertEquals(commentRepository.getReferenceById(2L), comment2,
                "Некорректное извлечение комментария с id = 2 из БД");
    }

    @Test
    void deleteComments() {
        commentRepository.deleteAllInBatch();
        assertTrue(commentRepository.findAll().isEmpty(),
                "После удаления всех комментариев список комментариев в БД не пуст");
    }

    @Test
    void findAllByItemId() {
        Item item1 = itemRepository.getReferenceById(1L);
        User user1 = userRepository.getReferenceById(1L);
        Comment comment1 = new Comment(null, "this is a 1st comment", item1, user1, LocalDateTime.now());
        commentRepository.save(comment1);
        User user2 = userRepository.getReferenceById(2L);
        Comment comment2 = new Comment(null, "this is a 2nd comment", item1, user2, LocalDateTime.now());
        commentRepository.save(comment2);

        List<Comment> allCommentsForItem1 = commentRepository.findAllByItem_Id(1L);
        assertTrue(allCommentsForItem1.size() == 2,
                "В БД неверное количество комментариев у предмета с id = 1");
        assertEquals(allCommentsForItem1.get(0).getText(), "this is a 1st comment",
                "Некорректное содержимое комментарий с id = 1");
        assertEquals(allCommentsForItem1.get(1).getAuthor().getId(), user2.getId(),
                "Некорректное id пользователя у комментария с id = 2");
    }
}