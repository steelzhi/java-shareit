package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOutForController;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.IllegalAccessException;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    BookingService bookingService = new BookingServiceImpl(
            bookingRepository,
            userRepository,
            itemRepository);

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@user.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@user.ru")
            .build();

    Item item1 = Item.builder()
            .id(1L)
            .name("УШМ")
            .description("Углошлифовальная машина")
            .available(true)
            .owner(user1)
            .build();

    Item item3 = Item.builder()
            .id(3L)
            .name("Зарядное устройство")
            .description("Для зарядки аккумуляторов")
            .available(false)
            .owner(user2)
            .build();

    BookingDtoIn bookingDtoIn1 = BookingDtoIn.builder()
            .id(1L)
            .start(LocalDateTime.now().plusHours(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .itemId(1L)
            .bookerId(2L)
            .status(BookingStatus.WAITING)
            .build();

    Booking booking1 = BookingMapper.mapToBooking(bookingDtoIn1, item1, user2);

    BookingDtoOutForController bookingDtoOutForController1 = BookingMapper.mapToBookingDtoOutForController(booking1);

    LocalDateTime now = LocalDateTime.now();

    @Test
    void createBooking() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item1);
        Mockito.when(userRepository.getReferenceById(2L))
                .thenReturn(user2);
        Mockito.when(bookingRepository.save(booking1))
                .thenReturn(booking1);

        BookingDtoOutForController createdBooking = bookingService.createBookingDto(bookingDtoIn1, 2L);
        assertThat(createdBooking, equalTo(bookingDtoOutForController1));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(userRepository, Mockito.times(1)).getReferenceById(2L);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).getReferenceById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(booking1);
    }

    @Test
    void createBookingWithNotAvailableItem() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(3L))
                .thenReturn(Optional.of(item3));
        Mockito.when(userRepository.getReferenceById(1L))
                .thenReturn(user1);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .id(2L)
                .itemId(3L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusDays(1L))
                .build();

        ItemNotAvailableException itemNotAvailableException = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBookingDto(bookingDtoIn, 1L));
        assertEquals(itemNotAvailableException.getMessage(), "Данная вещь в настоящий момент занята");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        ;
        Mockito.verify(userRepository, Mockito.never()).findById(2L);
        Mockito.verify(userRepository, Mockito.never()).getReferenceById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(3L);
        Mockito.verify(bookingRepository, Mockito.never()).save(BookingMapper.mapToBooking(bookingDtoIn, item3, user1));
    }

    @Test
    void createBookingWithoutDate() {
        Mockito.when(itemRepository.findById(3L))
                .thenReturn(Optional.of(item3));
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .id(2L)
                .itemId(3L)
                .build();

        IncorrectDateException incorrectDateException = assertThrows(IncorrectDateException.class,
                () -> bookingService.createBookingDto(bookingDtoIn, 1L));

        assertEquals(incorrectDateException.getMessage(), "Некорректная дата аренды");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(3L);
        Mockito.verify(bookingRepository, Mockito.never()).save(BookingMapper.mapToBooking(bookingDtoIn, item3, user1));
    }

    @Test
    void patchBookingWithUpdatedStatus() {
        BookingDtoIn updatedBookingDtoIn1 = BookingDtoIn.builder()
                .id(booking1.getId())
                .itemId(booking1.getItem().getId())
                .bookerId(booking1.getBooker().getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(BookingStatus.REJECTED)
                .build();

        Booking updatedBooking1 = BookingMapper.mapToBooking(updatedBookingDtoIn1, booking1.getItem(), booking1.getBooker());

        BookingDtoOutForController updatedBookingDtoOutForController1 = BookingMapper.mapToBookingDtoOutForController(updatedBooking1);

        Mockito.when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(itemRepository.getReferenceById(booking1.getItem().getId()))
                .thenReturn(booking1.getItem());
        assertThat(bookingService.patchBookingDtoWithUpdatedStatus(
                booking1.getId(), booking1.getItem().getOwner().getId(), false), equalTo(updatedBookingDtoOutForController1));

        Mockito.verify(bookingRepository, Mockito.times(1)).save(updatedBooking1);
        Mockito.verify(itemRepository, Mockito.never())
                .getReferenceById(booking1.getItem().getId());
    }

    @Test
    void patchBookingWithDuplicatedStatus() {
        booking1.setStatus(BookingStatus.APPROVED);
        BookingDtoIn updatedBookingDtoIn1 = BookingDtoIn.builder()
                .id(booking1.getId())
                .itemId(booking1.getItem().getId())
                .bookerId(booking1.getBooker().getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        Booking updatedBooking1 = BookingMapper.mapToBooking(updatedBookingDtoIn1, booking1.getItem(), booking1.getBooker());

        BookingDtoOutForController updatedBookingDtoOutForController1 = BookingMapper.mapToBookingDtoOutForController(updatedBooking1);

        Mockito.when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(itemRepository.getReferenceById(booking1.getItem().getId()))
                .thenReturn(booking1.getItem());

        DuplicateStatusException duplicateStatusException = assertThrows(DuplicateStatusException.class,
                () -> bookingService.patchBookingDtoWithUpdatedStatus(
                        updatedBooking1.getId(), booking1.getItem().getOwner().getId(), true));

        assertThat(duplicateStatusException.getMessage(), equalTo("Данный статус уже установлен ранее."));

        Mockito.verify(bookingRepository, Mockito.never()).save(updatedBooking1);
    }

    @Test
    void patchBookingByUserWithoutPatchingRights() {
        BookingDtoIn updatedBookingDtoIn1 = BookingDtoIn.builder()
                .id(booking1.getId())
                .itemId(booking1.getItem().getId())
                .bookerId(booking1.getBooker().getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        Booking updatedBooking1 = BookingMapper.mapToBooking(updatedBookingDtoIn1, booking1.getItem(), booking1.getBooker());

        BookingDtoOutForController updatedBookingDtoOutForController1 = BookingMapper.mapToBookingDtoOutForController(updatedBooking1);

        Mockito.when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(itemRepository.getReferenceById(booking1.getItem().getId()))
                .thenReturn(booking1.getItem());

        IllegalAccessException illegalAccessException = assertThrows(IllegalAccessException.class,
                () -> bookingService.patchBookingDtoWithUpdatedStatus(booking1.getId(), user2.getId(), true));

        assertEquals(illegalAccessException.getMessage(), "Пользователь с id = " + user2.getId() +
                " не имеет права доступа к информации о бронировании с id = " + booking1);

        Mockito.verify(itemRepository, Mockito.never())
                .getReferenceById(booking1.getItem().getId());
        Mockito.verify(bookingRepository, Mockito.never()).save(updatedBooking1);
    }

    @Test
    void getBooking() {
        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking1));
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item1);

        assertThat(bookingService.getBookingDto(1L, 2L), equalTo(bookingDtoOutForController1));

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(booking1.getId());
        Mockito.verify(itemRepository, Mockito.never()).getReferenceById(booking1.getItem().getId());
    }

    @Test
    void getBookingWithNoRightsToMakeRequest() {
        User user3 = User.builder()
                .id(3L)
                .build();
        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking1));
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item1);

        IllegalAccessException illegalAccessException = assertThrows(IllegalAccessException.class,
                () -> bookingService.getBookingDto(1L, 3L));

        assertEquals(illegalAccessException.getMessage(), "Пользователь с id = " + user3.getId() +
                " не имеет права доступа к информации о вещи с id = " + booking1);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(booking1.getId());
        Mockito.verify(itemRepository, Mockito.never()).getReferenceById(booking1.getItem().getId());
    }

    @Test
    void getAllBookingsByUserStatusWaiting() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user2)
                .start(LocalDateTime.now().plusDays(2L))
                .end(LocalDateTime.now().plusDays(3L))
                .status(BookingStatus.WAITING)
                .build();

        BookingDtoOutForController bookingDtoOutForController2 = BookingMapper.mapToBookingDtoOutForController(booking2);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsByBooker_Id(2L))
                .thenReturn(List.of(booking2, booking1));

        assertThat(bookingService.getAllBookingDtosByUser(2L, "WAITING", null, null),
                equalTo(List.of(bookingDtoOutForController2, bookingDtoOutForController1)));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllBookingsByBooker_Id(2L);
    }

    @Test
    void getAllBookingsByUserStatusCurrent() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user2)
                .start(LocalDateTime.now().plusDays(2L))
                .end(LocalDateTime.now().plusDays(3L))
                .status(BookingStatus.WAITING)
                .build();

        BookingDtoOutForController bookingDtoOutForController2 = BookingMapper.mapToBookingDtoOutForController(booking2);


        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsByBooker_Id(2L))
                .thenReturn(List.of(booking1, booking2));

        assertThat(bookingService.getAllBookingDtosByUser(2L, "CURRENT", null, null),
                equalTo(new ArrayList<>()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllBookingsByBooker_Id(2L);
    }

    @Test
    void getAllBookingsByUserStatusPast() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user2)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .status(BookingStatus.WAITING)
                .build();

        BookingDtoOutForController bookingDtoOutForController2 = BookingMapper.mapToBookingDtoOutForController(booking2);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsByBooker_Id(2L))
                .thenReturn(List.of(booking1, booking2));

        assertThat(bookingService.getAllBookingDtosByUser(2L, "PAST", null, null),
                equalTo(new ArrayList<>()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllBookingsByBooker_Id(2L);
    }

    @Test
    void getAllBookingsByUserStatusRejected() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user2)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .status(BookingStatus.WAITING)
                .build();

        BookingDtoOutForController bookingDtoOutForController2 = BookingMapper.mapToBookingDtoOutForController(booking2);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsByBooker_Id(2L))
                .thenReturn(List.of(booking1, booking2));

        assertThat(bookingService.getAllBookingDtosByUser(2L, "REJECTED", null, null),
                equalTo(new ArrayList<>()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllBookingsByBooker_Id(2L);
    }

    @Test
    void getAllBookingsByUserStatusAll() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user2)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .status(BookingStatus.WAITING)
                .build();

        BookingDtoOutForController bookingDtoOutForController2 = BookingMapper.mapToBookingDtoOutForController(booking2);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsByBooker_Id(2L))
                .thenReturn(List.of(booking2, booking1));

        assertThat(bookingService.getAllBookingDtosByUser(2L, "ALL", null, null),
                equalTo(List.of(bookingDtoOutForController2, bookingDtoOutForController1)));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllBookingsByBooker_Id(2L);
    }

    @Test
    void getAllBookingsByUserStatusFuture() {
        booking1.setStart(now.plusHours(1L));
        booking1.setEnd(now.plusDays(1L));

        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user2)
                .start(now)
                .end(now)
                .status(BookingStatus.WAITING)
                .build();

        BookingDtoOutForController bookingDtoOutForController2 = BookingMapper.mapToBookingDtoOutForController(booking2);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsByBooker_Id(2L))
                .thenReturn(List.of(booking1, booking2));

        assertThat(bookingService.getAllBookingDtosByUser(2L, "FUTURE", null, null),
                equalTo(List.of(bookingDtoOutForController1, bookingDtoOutForController2)));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1)).getAllBookingsByBooker_Id(2L);
    }

    @Test
    void getAllBookingsByUserWithPagination() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Page<Booking> pagedList = new PageImpl(List.of(booking1));
        Mockito.when(bookingRepository.getAllBookingsByBooker_Id(2L, getPage(0, 10)))
                .thenReturn(pagedList);

        assertThat(bookingService.getAllBookingDtosByUser(2L, "WAITING", 0, 10),
                equalTo(BookingMapper.mapToBookingDtoOutForController(pagedList.getContent())));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsByBooker_Id(2L, getPage(0, 10));
    }

    @Test
    void getAllBookingsForUserItemsWithNotEmptyItems() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.getAllBookingsForOwnerItems(1L))
                .thenReturn(List.of(booking1));

        assertThat(bookingService.getAllBookingDtosForUserItems(1L, "WAITING", null, null),
                equalTo(BookingMapper.mapToBookingDtoOutForController(List.of(booking1))));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsForOwnerItems(1L);
    }

    @Test
    void getAllBookingsForUserItemsWithEmptyItems() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsForOwnerItems(2L))
                .thenReturn(new ArrayList<>());

        assertThat(bookingService.getAllBookingDtosForUserItems(2L, "WAITING", null, null),
                equalTo(new ArrayList<>()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsForOwnerItems(2L);
    }

    @Test
    void getAllBookingsForUserItemsWithIncorrectStatus() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsForOwnerItems(2L))
                .thenReturn(new ArrayList<>());

        WrongBookingStatusException wrongBookingStatusException = assertThrows(WrongBookingStatusException.class,
                () -> bookingService.getAllBookingDtosForUserItems(
                        2L, "INCORRECT STATUS", null, null));

        assertThat(wrongBookingStatusException.getMessage(),
                equalTo("Введен некорректный статус бронирования"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsForOwnerItems(2L);
    }

    @Test
    void getAllBookingsForUserItemsWithPagination() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Page<Booking> pagedList = new PageImpl(List.of(booking1));

        Mockito.when(bookingRepository.getAllBookingsForOwnerItems(1L, getPage(0, 10)))
                .thenReturn(pagedList);

        assertThat(bookingService.getAllBookingDtosForUserItems(1L, "WAITING", 0, 10),
                equalTo(BookingMapper.mapToBookingDtoOutForController(pagedList.getContent())));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsForOwnerItems(1L, getPage(0, 10));
    }

    PageRequest getPage(Integer from, Integer size) {
        if (from != null && size != null) {
            return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
        }
        return null;
    }
}
