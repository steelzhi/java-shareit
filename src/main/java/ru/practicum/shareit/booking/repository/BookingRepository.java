package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

/*    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN Booker as br " +
            "WHERE br.id = ?1 " +
            "AND b.status IN ?2")*/
/*    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "WHERE b.id = ?1 " +
            "AND b.status IN ?2", nativeQuery = true)*/
    //List<Booking> getAllBookingsByBooker_IdAndStatusIn(Long bookerId, List<BookingStatus> bookingStatusNamesForBookers);

    List<Booking> getAllBookingsByBooker_Id(Long bookerId);


    /*    @Query(value = "SELECT b " +
                "FROM bookings AS b " +
                "JOIN items AS i ON i.id = b.item_id " +
                "JOIN users AS u ON u.id = i.user_id " +
                "WHERE u.id = ?1 " +
                "AND i.status IN ?2", nativeQuery = true)
        List<Booking> getAllBookingsForUserItems(Long userId, List<String> bookingStatusNamesForItems);*/
    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON i.id = b.item_id " +
            "JOIN users AS u ON u.id = i.user_id " +
            "WHERE u.id = ?1", nativeQuery = true)
    List<Booking> getAllBookingsForUserItems(Long id);
}
