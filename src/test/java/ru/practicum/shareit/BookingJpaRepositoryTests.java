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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
public class BookingJpaRepositoryTests {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

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
    void getAllBookings() {
        List<Booking> allBookings = bookingRepository.findAll();
        assertEquals(allBookings.size(), 3, "Некорректное количество бронирований в БД");
    }

    @Test
    void get1stBooking() {
        Booking booking = bookingRepository.getReferenceById(1L);

        assertEquals(booking.getBooker().getId(), 1L,
                "Некорректный id пользователя, забронировавшего предмет");
        assertEquals(booking.getStatus(), BookingStatus.WAITING,
                "Некорректный статус, присваеваемый по умолчанию бронированию");
        assertEquals(booking.getItem().getId(), 1L,
                "Некорректный id забронированного предмета");
    }


    @Test
    void deleteItems() {
        bookingRepository.deleteAllInBatch();
        assertTrue(bookingRepository.findAll().isEmpty(),
                "После удаления всех бронирований список бронирований в БД не пуст");
    }

    @Test
    void getAllBookingsByBooker() {
        List<Booking> allBookingsByUser1 = bookingRepository.getAllBookingsByBooker_Id(1L);
        assertTrue(allBookingsByUser1.size() == 1,
                "Некорректное количество бронирований в БД у пользователя с id = 1 ");
        assertEquals(allBookingsByUser1.get(0).getItem().getId(), 1L,
                "Некорректное id у предмета, забронированного пользователем с id = 1");

        List<Booking> allBookingsByNotExistingUser = bookingRepository.getAllBookingsByBooker_Id(100L);

        assertTrue(allBookingsByNotExistingUser.isEmpty(),
                "Найдены бронирования у несуществующего пользователя");
    }

    @Test
    void getAllBookingsForUserItems() {
        User user1 = userRepository.getReferenceById(1L);

        Item item4 = new Item(
                null,
                "Бензопила",
                "Бензопила DeWalt",
                false,
                user1,
                null);
        itemRepository.save(item4);

        Booking booking4 = new Booking(null,
                LocalDateTime.now().plusHours(11L),
                LocalDateTime.now().plusDays(31L),
                itemRepository.getReferenceById(4L),
                userRepository.getReferenceById(2L),
                null);
        bookingRepository.save(booking4);

        List<Booking> bookingsForUser1Items = bookingRepository.getAllBookingsForUserItems(1L);
        assertTrue(bookingsForUser1Items.size() == 2,
                "Некорректный размер списка бронирования предметов пользователя с id = 1");
        assertEquals(bookingsForUser1Items, List.of(booking2, booking4),
                "Некорректный состав бронирований предметов пользователя с id = 1");
    }

    @Test
    void getAllBookingsByOwnerIdAndItemId() {
        Booking booking4 = new Booking(null,
                LocalDateTime.now().plusHours(11L),
                LocalDateTime.now().plusDays(31L),
                itemRepository.getReferenceById(1L),
                userRepository.getReferenceById(2L),
                null);
        bookingRepository.save(booking4);

        List<Booking> allBookingsByOwner3AndItem1 =
                bookingRepository.getAllBookingsByOwner_IdAndItem_Id(3L, 1L);
        assertTrue(allBookingsByOwner3AndItem1.size() == 2,
                "Некорректный размер списка бронирования предмета с id = 1 пользователя с id = 3");
        assertEquals(allBookingsByOwner3AndItem1, List.of(booking1, booking4),
                "Некорректный состав бронирований предмета с id = 1 пользователя с id = 3");
    }

    @Test
    void getAllBookingsForItemId() {
        List<Booking> allBookingsForItem2 = bookingRepository.getAllBookingsForItem_Id(2L);
        assertEquals(allBookingsForItem2.size(), 1,
                "Некорректный размер списка бронирования предмета с id = 2");
        assertEquals(allBookingsForItem2, List.of(booking2),
                "Некорректный состав бронирований предмета с id = 2");
    }
}