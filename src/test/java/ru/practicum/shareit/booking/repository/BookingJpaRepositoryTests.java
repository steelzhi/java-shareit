/*
package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
    TestEntityManager em;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void getAllBookingsByBookerId() {
        List<Booking> bookingsByUserId = bookingRepository.getAllBookingsByBooker_Id(1L);

        assertThat(bookingsByUserId.size(), equalTo(5));
        assertThat(bookingsByUserId, equalTo(List.of(
                bookingRepository.getReferenceById(1L),
                bookingRepository.getReferenceById(2L),
                bookingRepository.getReferenceById(5L),
                bookingRepository.getReferenceById(6L),
                bookingRepository.getReferenceById(8L))));
    }

    @Test
    void getAllBookingsForOwnerItems() {
        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("SELECT b FROM Booking AS b JOIN b.item AS i JOIN i.owner AS o WHERE o.id = :id",
                        Booking.class);

        List<Booking> bookingsForOwner1Items = List.of(
                bookingRepository.getReferenceById(3L),
                bookingRepository.getReferenceById(7L));

        List<Booking> foundBookings = query.setParameter("id", 1L).getResultList();

        assertThat(bookingsForOwner1Items, equalTo(foundBookings));
    }

    @Test
    void getAllBookingsByOwnerIdAndItemId() {
        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery(
                        "SELECT b FROM Booking AS b JOIN b.item AS i " +
                                "JOIN i.owner AS o WHERE o.id = :oid AND i.id = :iid",
                        Booking.class);

        List<Booking> bookingsByOwnerId4AndItemId2 = List.of(
                bookingRepository.getReferenceById(1L),
                bookingRepository.getReferenceById(2L),
                bookingRepository.getReferenceById(4L),
                bookingRepository.getReferenceById(6L)
        );

        List<Booking> foundBookings = query
                .setParameter("oid", 4L)
                .setParameter("iid", 2L)
                .getResultList();

        assertThat(bookingsByOwnerId4AndItemId2, equalTo(foundBookings));
    }

    @Test
    void findAllBookingsByItemId() {
        List<Booking> allBookingsByItemId3 = bookingRepository.findAllBookingsByItem_Id(3L);

        assertThat(allBookingsByItemId3.size(), equalTo(1));
        assertThat(allBookingsByItemId3, equalTo(List.of(
                bookingRepository.getReferenceById(5L))));
    }
}
*/
