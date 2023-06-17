package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.junit.jupiter.api.Assertions.*;

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

    List<User> users = List.of(user1, user2);

    Item item1 = Item.builder()
            .id(1L)
            .name("УШМ")
            .description("Углошлифовальная машина")
            .available(true)
            .owner(user1)
            .build();

    Item item2 = Item.builder()
            .id(2L)
            .name("Канистра")
            .description("Для ГСМ, V = 20 л")
            .available(true)
            .owner(user2)
            .build();

    Item item3 = Item.builder()
            .id(3L)
            .name("Зарядное устройство")
            .description("Для зарядки аккумуляторов")
            .available(false)
            .owner(user2)
            .build();

    Booking booking1 = Booking.builder()
            .id(1L)
            .item(item1)
            .booker(user2)
            .start(LocalDateTime.now().plusHours(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .status(BookingStatus.WAITING)
            .build();

    BookingDto bookingDto = BookingMapper.mapToBookingDto(booking1);

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
        Mockito.when(bookingService.createBooking(bookingDto, 2L))
                .thenReturn(booking1);

        Booking createdBooking = bookingService.createBooking(bookingDto, 2L);
        assertThat(createdBooking, equalTo(booking1));

        Mockito.verify(userRepository, Mockito.times(2)).findById(2L);
        Mockito.verify(userRepository, Mockito.times(2)).getReferenceById(2L);
        Mockito.verify(itemRepository, Mockito.times(2)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(2)).getReferenceById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(createdBooking);
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

        BookingDto bookingDto = BookingDto.builder()
                .id(2L)
                .itemId(3L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusDays(1L))
                .build();

        ItemNotAvailableException itemNotAvailableException = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(bookingDto, 1L));
        assertEquals(itemNotAvailableException.getMessage(), "Данная вещь в настоящий момент занята");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);;
        Mockito.verify(userRepository, Mockito.never()).findById(2L);
        Mockito.verify(userRepository, Mockito.never()).getReferenceById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(3L);
        Mockito.verify(bookingRepository, Mockito.never()).save(BookingMapper.mapToBooking(bookingDto, item3, user1));
    }

    @Test
    void createBookingWithoutDate() {
        Mockito.when(itemRepository.findById(3L))
                .thenReturn(Optional.of(item3));
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        BookingDto bookingDto = BookingDto.builder()
                .id(2L)
                .itemId(3L)
                .build();

        IncorrectDateException incorrectDateException = assertThrows(IncorrectDateException.class,
                () -> bookingService.createBooking(bookingDto, 1L));

        assertEquals(incorrectDateException.getMessage(), "Некорректная дата аренды");

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(3L);
        Mockito.verify(bookingRepository, Mockito.never()).save(BookingMapper.mapToBooking(bookingDto, item3, user1));
    }

    @Test
    void patchBookingWithUpdatedStatus() {
        Booking updatedBooking1 = Booking.builder()
                .id(booking1.getId())
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(bookingRepository.findById(booking1.getId()))
                        .thenReturn(Optional.of(booking1));
        Mockito.when(itemRepository.getReferenceById(booking1.getItem().getId()))
                        .thenReturn(booking1.getItem());
        Mockito.when(bookingService.patchBookingWithUpdatedStatus(
        booking1.getId(), booking1.getItem().getOwner().getId(), true))

                .thenReturn(booking1);

        assertThat(booking1, equalTo(updatedBooking1));

        Mockito.verify(bookingRepository, Mockito.times(1)).save(updatedBooking1);
        Mockito.verify(itemRepository, Mockito.never())
                .getReferenceById(booking1.getItem().getId());
    }

    @Test
    void patchBookingByUserWithoutPatchingRights() {
        Booking updatedBooking1 = Booking.builder()
                .id(booking1.getId())
                .item(booking1.getItem())
                .booker(booking1.getBooker())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(itemRepository.getReferenceById(booking1.getItem().getId()))
                .thenReturn(booking1.getItem());

        IllegalAccessException illegalAccessException = assertThrows(IllegalAccessException.class,
                () -> bookingService.patchBookingWithUpdatedStatus(booking1.getId(), user2.getId(), true));

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

        assertThat(bookingService.getBooking(1L, 2L), equalTo(booking1));

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
                () -> bookingService.getBooking(1L, 3L));

        assertEquals(illegalAccessException.getMessage(), "Пользователь с id = " + user3.getId() +
                " не имеет права доступа к информации о вещи с id = " + booking1);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(booking1.getId());
        Mockito.verify(itemRepository, Mockito.never()).getReferenceById(booking1.getItem().getId());
    }

    @Test
    void getAllBookingsByUser() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(bookingRepository.getAllBookingsByBooker_Id(2L))
                .thenReturn(List.of(booking1));

        assertThat(bookingService.getAllBookingsByUser(2L, "WAITING", null, null),
                equalTo(List.of(booking1)));

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

        assertThat(bookingService.getAllBookingsByUser(2L, "WAITING", 0, 10),
                equalTo(pagedList.getContent()));

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

        assertThat(bookingService.getAllBookingsForUserItems(1L, "WAITING", null, null),
                equalTo(List.of(booking1)));

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

        assertThat(bookingService.getAllBookingsForUserItems(2L, "WAITING", null, null),
                equalTo(new ArrayList<>()));

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

        assertThat(bookingService.getAllBookingsForUserItems(1L, "WAITING", 0, 10),
                equalTo(pagedList.getContent()));

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