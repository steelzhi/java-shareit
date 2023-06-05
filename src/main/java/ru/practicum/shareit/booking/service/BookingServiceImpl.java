package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.IllegalAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemDtoRepository;

    @Override
    public Booking createBooking(BookingDto bookingDto, Long userId) {
        checkIfTheItemIsAvailableAntOtherParamsAreCorrect(bookingDto, userId);
        bookingDto.setStatus(BookingStatus.WAITING);
        Long itemDtoId = bookingDto.getItemId();
        Booking booking = BookingMapper.mapToBooking(bookingDto, itemDtoRepository.getReferenceById(itemDtoId),
                userRepository.getReferenceById(userId));
        return bookingRepository.save(booking);
    }

    @Override
    public Booking patchBookingWithUpdatedStatus(Long bookingId, Long userId, Boolean approved) {
        Booking booking = getBookingIfUserHasPatchingRights(bookingId, userId);
        BookingStatus currentStatus = booking.getStatus();
        String statusName = (approved == true) ? BookingStatus.APPROVED.name() : BookingStatus.REJECTED.name();
        BookingStatus newStatus = BookingStatus.valueOf(statusName);
        if (currentStatus == newStatus) {
            throw new DuplicateStatusException("Данный статус уже установлен ранее.");
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        return bookingRepository.getReferenceById(bookingId);
    }

    @Override
    public Booking getBooking(Long bookingId, Long userId) {
        return getBookingIfUserHasAccessRights(bookingId, userId);
    }

    @Override
    public List<Booking> getAllBookingsByUser(Long userId, String bookingStatus) {
        checkIfUserExists(userId);

        List<Booking> userBookings = bookingRepository.getAllBookingsByBooker_Id(userId);
        return getBookingsWithDemandedStatus(userBookings, bookingStatus);
    }

    @Override
    public List<Booking> getAllBookingsForUserItems(Long userId, String bookingStatus) {
        checkIfUserExists(userId);

        List<Booking> itemBookings =
                bookingRepository.getAllBookingsForUserItems(userId); // Здесь должен быть другой метод, а getAllBookingsForUserItems
        return getBookingsWithDemandedStatus(itemBookings, bookingStatus);
    }

    private List<Booking> getBookingsWithDemandedStatus(List<Booking> bookings, String bookingStatus) {
        List<Booking> userBookingsWithDemandedStatus = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now().minusSeconds(5);
        if (bookingStatus == null) {
            bookingStatus = "ALL";
        }

        try {
            BookingStatus.valueOf(bookingStatus);
        } catch (IllegalArgumentException e) {
            throw new WrongBookingStatusException("Введен некорректный статус бронирования");
        }

        for (Booking booking : bookings) {
            switch (bookingStatus) {
                case "CURRENT":
                    if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case "PAST":
                    if (booking.getEnd().isBefore(now)) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case "FUTURE":
                    if (booking.getStart().isAfter(now)) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case "WAITING":
                    if (booking.getStatus() == BookingStatus.WAITING) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case "REJECTED":
                    if (booking.getStatus() == BookingStatus.REJECTED) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case "ALL":
                    userBookingsWithDemandedStatus.add(booking);
                    break;
                default:
                    throw new WrongBookingStatusException("Введен некорректный статус бронирования");
            }
        }
        return getBookingSortedByDateTime(userBookingsWithDemandedStatus);
    }

    private User getUserIfHeHasEvenOneItem(Long userId) {
        checkIfUserExists(userId);
        List<Item> allItemsDto = itemDtoRepository.findAll();
        for (Item itemDto : allItemsDto) {
            if (itemDto.getOwner().getId() == userId) {
                return userRepository.getReferenceById(userId);
            }
        }

        throw new UserDoesNotHaveItemsException("У данного пользователя нет ни одной вещи.");
    }

    private Booking getBookingIfUserHasAccessRights(Long bookingId, Long userId) {
        checkIfBookingExists(bookingId);

        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item itemDto = itemDtoRepository.getReferenceById(booking.getItem().getId());
        if (booking.getBooker().getId() != userId && itemDto.getOwner().getId() != userId) {
            throw new IllegalAccessException(
                    "Пользователь с id = " + userId + " не имеет права доступа к информации о вещи с id = " + booking);
        }
        return booking;
    }

    private Booking getBookingIfUserHasPatchingRights(Long bookingId, Long userId) {
        checkIfBookingExists(bookingId);

        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item itemDto = itemDtoRepository.getReferenceById(booking.getItem().getId());
        if (itemDto.getOwner().getId() != userId) {
            throw new IllegalAccessException(
                    "Пользователь с id = " + userId + " не имеет права доступа к информации о бронировании с id = "
                            + booking);
        }
        return booking;
    }

    private void checkIfBookingExists(Long bookingId) {
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if (booking.getId() == bookingId) {
                return;
            }
        }

        throw new BookingDoesNotExistException("Бронирования с id = " + bookingId + "не найдено.");
    }

    private List<Booking> getBookingSortedByDateTime(List<Booking> unsortedBookings) {
        unsortedBookings.sort((booking1, booking2) -> {
            if (booking1.getEnd().isAfter(booking2.getEnd())) {
                return -1;
            } else {
                return 1;
            }
        });
        return unsortedBookings;
    }

    private void checkIfTheItemIsAvailableAntOtherParamsAreCorrect(BookingDto bookingDto, Long userId) {
        checkIfUserExists(userId);

        try {
            Item itemDto = itemDtoRepository.getReferenceById(bookingDto.getItemId());
            if (bookingDto.getEnd() == null
                    || bookingDto.getStart() == null
                    || bookingDto.getEnd().isBefore(LocalDateTime.now())
                    || bookingDto.getStart().isBefore(LocalDateTime.now())
                    || bookingDto.getEnd().isBefore(bookingDto.getStart())
                    || bookingDto.getEnd().equals(bookingDto.getStart())) {
                throw new IncorrectDateException("Некорректная дата аренды");
            }
            if (!itemDto.getAvailable()) {
                throw new ItemNotAvailableException("Данная вещь в настоящий момент занята");
            }
            if (userId == itemDto.getOwner().getId()) {
                throw new IllegalBookingAttemptException("Владелец вещи не может бронировать свою вещь");
            }
        } catch (EntityNotFoundException e) {
            throw new ItemDoesNotExistException("Вещи с таким id не существует");
        }
    }

    private void checkIfUserExists(Long userId) {
        try {
            User user = userRepository.getReferenceById(userId);
            System.out.println(user);
        } catch (EntityNotFoundException e) {
            throw new UserDoesNotExistException("Пользователя с таким id не существует");
        }
    }
}

/*    private List<BookingStatus> bookingStatusNamesForBookers(BookingStatus bookingStatus) {
        if (bookingStatus == BookingStatus.ALL) {
            return List.of(BookingStatus.CURRENT,
                    BookingStatus.PAST,
                    BookingStatus.FUTURE,
                    BookingStatus.WAITING,
                    BookingStatus.REJECTED,
                    BookingStatus.APPROVED);
        } else {
            return List.of(bookingStatus);
        }
    }

    private List<BookingStatus> bookingStatusNamesForItems(BookingStatus bookingStatus) {
        if (bookingStatus == BookingStatus.ALL) {
            return List.of(BookingStatus.WAITING,
                    BookingStatus.APPROVED,
                    BookingStatus.REJECTED,
                    BookingStatus.CANCELED);
        } else {
            return List.of(bookingStatus);
        }
    }*/
