package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getAllBookingsByBooker_Id(Long bookerId);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON i.id = b.item_id " +
            "JOIN users AS u ON u.id = i.user_id " +
            "WHERE u.id = ?1", nativeQuery = true)
    List<Booking> getAllBookingsForUserItems(Long id);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON i.id = b.item_id " +
            "JOIN users AS u ON u.id = i.user_id " +
            "WHERE u.id = ?1 " +
            "AND i.id = ?2", nativeQuery = true)
    List<Booking> getAllBookingsByOwner_IdAndItem_Id(Long userId, Long itemId);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "JOIN items AS i ON i.id = b.item_id " +
            "AND i.id = ?1", nativeQuery = true)
    List<Booking> getAllBookingsForItem_Id(Long itemId);
}
