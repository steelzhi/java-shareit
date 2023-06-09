package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getAllBookingsByBooker_Id(long bookerId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "JOIN i.owner AS o " +
            "WHERE o.id = ?1")
    List<Booking> getAllBookingsForOwnerItems(long ownerId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN b.item AS i " +
            "JOIN i.owner AS o " +
            "WHERE o.id = ?1 " +
            "AND i.id = ?2")
    List<Booking> getAllBookingsByOwner_IdAndItem_Id(long ownerId, long itemId);

    List<Booking> findAllBookingsByItem_Id(long itemId);
}
