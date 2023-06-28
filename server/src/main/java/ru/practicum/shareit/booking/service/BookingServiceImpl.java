package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutForController;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.IllegalAccessException;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final long delay = 500_000_000;

    @Override
    @Transactional
    public BookingDtoOutForController createBookingDto(BookingDtoIn bookingDto, long userId) {
        checkIfTheItemIsAvailableAntOtherParamsAreCorrect(bookingDto, userId);
        bookingDto.setStatus(BookingStatus.WAITING);
        Long itemDtoId = bookingDto.getItemId();
        Booking booking = BookingMapper.mapToBooking(bookingDto, itemRepository.getReferenceById(itemDtoId),
                userRepository.getReferenceById(userId));
        Booking savedBooking = bookingRepository.save(booking);
        BookingDtoOutForController outcomingBookingDto = BookingMapper.mapToBookingDtoOutForController(booking);
        return outcomingBookingDto;
    }

    @Override
    @Transactional
    public BookingDtoOutForController patchBookingDtoWithUpdatedStatus(long bookingId, long userId, Boolean approved) {
        Booking booking = getBookingIfUserHasPatchingRights(bookingId, userId);
        BookingStatus currentStatus = booking.getStatus();
        String statusName = (approved == true) ? BookingStatus.APPROVED.name() : BookingStatus.REJECTED.name();
        BookingStatus newStatus = BookingStatus.valueOf(statusName);
        if (currentStatus == newStatus) {
            throw new DuplicateStatusException("Данный статус уже установлен ранее.");
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        BookingDtoOutForController bookingDto = BookingMapper.mapToBookingDtoOutForController(booking);
        return bookingDto;
    }

    @Override
    public BookingDtoOutForController getBookingDto(long bookingId, long userId) {
        Booking booking = getBookingIfUserHasAccessRights(bookingId, userId);
        BookingDtoOutForController bookingDto = BookingMapper.mapToBookingDtoOutForController(booking);
        return bookingDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOutForController> getAllBookingDtosByUser(
            long userId, String bookingStatus, Integer from, Integer size) {
        checkIfUserExists(userId);

        List<Booking> userBookings = new ArrayList<>();
        if (from != null && size != null) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
            Page<Booking> pagedList = bookingRepository.getAllBookingsByBooker_Id(userId, page);
            if (pagedList != null) {
                userBookings = pagedList.getContent();
            }
        } else {
            userBookings = bookingRepository.getAllBookingsByBooker_Id(userId);
        }

        List<Booking> bookings = getBookingsWithDemandedStatus(userBookings, bookingStatus);
        List<BookingDtoOutForController> bookingDtos = BookingMapper.mapToBookingDtoOutForController(bookings);
        return bookingDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOutForController> getAllBookingDtosForUserItems(
            long userId, String bookingStatus, Integer from, Integer size) {
        checkIfUserExists(userId);

        List<Booking> itemBookings = new ArrayList<>();
        if (from != null && size != null) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
            Page<Booking> pagedList = bookingRepository.getAllBookingsForOwnerItems(userId, page);
            if (pagedList != null) {
                itemBookings = pagedList.getContent();
            }
        } else {
            itemBookings = bookingRepository.getAllBookingsForOwnerItems(userId);
        }

        List<Booking> bookings = getBookingsWithDemandedStatus(itemBookings, bookingStatus);
        List<BookingDtoOutForController> bookingDtos = BookingMapper.mapToBookingDtoOutForController(bookings);
        return bookingDtos;
    }

    private List<Booking> getBookingsWithDemandedStatus(List<Booking> bookings, String status) {
        List<Booking> userBookingsWithDemandedStatus = new ArrayList<>();
        if (status == null) {
            status = "ALL";
        }

        BookingStatus bookingStatus;
        try {
            bookingStatus = BookingStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new WrongBookingStatusException("Введен некорректный статус бронирования");
        }

        for (Booking booking : bookings) {
            LocalDateTime now = LocalDateTime.now().minusNanos(delay);

            switch (bookingStatus) {
                case CURRENT:
                    if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case PAST:
                    if (booking.getEnd().isBefore(now)) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case FUTURE:
                    if (booking.getStart().isAfter(now)) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case WAITING:
                    if (booking.getStatus() == BookingStatus.WAITING) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case REJECTED:
                    if (booking.getStatus() == BookingStatus.REJECTED) {
                        userBookingsWithDemandedStatus.add(booking);
                    }
                    break;
                case ALL:
                    userBookingsWithDemandedStatus.add(booking);
            }
        }
        return getBookingSortedByDateTime(userBookingsWithDemandedStatus);
    }

    @Transactional(readOnly = true)
    private Booking getBookingIfUserHasAccessRights(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingDoesNotExistException("Бронирования с id = " + bookingId + "не найдено."));
        Item item = booking.getItem();
        if (booking.getBooker().getId() != userId && item.getOwner().getId() != userId) {
            throw new IllegalAccessException(
                    "Пользователь с id = " + userId + " не имеет права доступа к информации о вещи с id = " + booking);
        }
        return booking;
    }

    @Transactional(readOnly = true)
    private Booking getBookingIfUserHasPatchingRights(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingDoesNotExistException("Бронирования с id = " + bookingId + "не найдено."));
        Item item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            throw new IllegalAccessException(
                    "Пользователь с id = " + userId + " не имеет права доступа к информации о бронировании с id = "
                            + booking);
        }
        return booking;
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

    @Transactional(readOnly = true)
    private void checkIfTheItemIsAvailableAntOtherParamsAreCorrect(BookingDtoIn bookingDto, long userId) {
        checkIfUserExists(userId);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemDoesNotExistException("Вещи с таким id не существует"));

        if (bookingDto.getEnd() == null
                || bookingDto.getStart() == null
                || bookingDto.getEnd().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isBefore(LocalDateTime.now())
                || bookingDto.getEnd().isBefore(bookingDto.getStart())
                || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new IncorrectDateException("Некорректная дата аренды");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Данная вещь в настоящий момент занята");
        }
        if (userId == item.getOwner().getId()) {
            throw new IllegalBookingAttemptException("Владелец вещи не может бронировать свою вещь");
        }
    }

    private void checkIfUserExists(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Пользователя с таким id не существует"));
    }
}
