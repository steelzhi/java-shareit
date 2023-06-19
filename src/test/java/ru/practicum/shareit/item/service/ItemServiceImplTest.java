/*
package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class ItemServiceImplTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    ItemService itemService = new ItemServiceImpl(itemRepository,
            userRepository,
            bookingRepository,
            commentRepository);

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

    LocalDateTime now = LocalDateTime.now();

    Booking booking1 = Booking.builder()
            .id(1L)
            .item(item1)
            .booker(user2)
            .start(now)
            .end(now.plusDays(1L))
            .status(BookingStatus.WAITING)
            .build();

    Comment comment1 = Comment.builder()
            .id(1L)
            .text("this is a comment")
            .item(item1)
            .author(user2)
            .created(now)
            .build();

    Comment comment2 = Comment.builder()
            .id(2L)
            .text("this is a 2nd comment")
            .item(item1)
            .author(user2)
            .created(now)
            .build();

    CommentDto commentDto = CommentMapper.mapToCommentDto(comment1);
    CommentDto commentDto2 = CommentMapper.mapToCommentDto(comment2);
    List<CommentDto> commentDtos = CommentMapper.mapToCommentDto(List.of(comment1, comment2));

    @Test
    void postItemWithExistingUserId() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemService.postItem(item1, 1L))
                .thenReturn(item1);

        Item postedItem1 = itemService.postItem(item1, 1L);
        assertEquals(item1, postedItem1, "Выгруженная из БД вещь не совпадает с первоначальной");

        Mockito.verify(itemRepository, Mockito.times(1)).save(item1);
        Mockito.verify(userRepository, Mockito.atMost(2)).findById(1L);
    }

    @Test
    void postItemWithNotExistingUserId() {
        Mockito.when(userRepository.findById(2L))
                .thenThrow(new UserDoesNotExistException("Пользователя с таким id не существует"));

        UserDoesNotExistException userDoesNotExistException =
                assertThrows(UserDoesNotExistException.class, () -> itemService.postItem(item1, 2L));
        assertEquals(userDoesNotExistException.getMessage(), "Пользователя с таким id не существует");

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(itemRepository, Mockito.never()).save(item1);
    }

    @Test
    void patchItemWithCorrectParams() {
        Item updatedItem2 = Item.builder()
                .name("Канистра")
                .description("Для ГСМ, V = 12 л")
                .available(false)
                .owner(user2)
                .build();

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findAllByOwner_Id(2L))
                .thenReturn(List.of(item2));
        Mockito.when(itemRepository.getReferenceById(2L))
                .thenReturn(item2);
        Mockito.when(itemRepository.findById(2L))
                .thenReturn(Optional.of(item2));
        Mockito.when(itemService.patchItem(2L, updatedItem2, 2L))
                .thenReturn(updatedItem2);

        Item patchedItem2 = itemService.patchItem(2L, updatedItem2, 2L);
        assertEquals(updatedItem2, patchedItem2, "Некорректное обновление вещи с id = 2");

        Mockito.verify(itemRepository, Mockito.times(1)).save(updatedItem2);
        Mockito.verify(userRepository, Mockito.atMost(2)).findById(2L);
    }

    @Test
    void patchItemWithUserWithNoItems() {
        Item updatedItem2 = Item.builder()
                .id(2L)
                .name("Канистра")
                .description("Для ГСМ, V = 12 л")
                .available(false)
                .owner(user2)
                .build();

        User user3 = User.builder()
                .id(3L)
                .build();

        Mockito.when(userRepository.findById(3L))
                .thenReturn(Optional.of(user3));
        Mockito.when(itemRepository.findAllByOwner_Id(3L))
                .thenReturn(new ArrayList<>());

        UserDoesNotExistOrDoesNotHaveAnyItemsException userDoesNotExistOrDoesNotHaveAnyItemsException =
                assertThrows(UserDoesNotExistOrDoesNotHaveAnyItemsException.class,
                        () -> itemService.patchItem(2L, updatedItem2, 3L));
        assertEquals(userDoesNotExistOrDoesNotHaveAnyItemsException.getMessage(),
                "Пользователя с указанным id не существует либо пользователь не добавил ни одной вещи.");

        Mockito.verify(itemRepository, Mockito.never()).save(updatedItem2);
        Mockito.verify(userRepository, Mockito.atMost(2)).findById(3L);
    }

    @Test
    void patchItemWithUserWithoutRightsToPatch() {
        Item updatedItem2 = Item.builder()
                .id(2L)
                .name("Канистра")
                .description("Для ГСМ, V = 12 л")
                .available(false)
                .owner(user2)
                .build();

        Mockito.when(itemRepository.getReferenceById(2L))
                .thenReturn(item2);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(itemRepository.findAllByOwner_Id(1L))
                .thenReturn(List.of(item1));
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item1);

        ItemDoesNotExistException itemDoesNotExistException = assertThrows(ItemDoesNotExistException.class,
                () -> itemService.patchItem(2L, updatedItem2, 1L));

        assertEquals(itemDoesNotExistException.getMessage(), "У пользователя нет вещи с указанным id.");

        Mockito.verify(itemRepository, Mockito.never()).save(updatedItem2);
        Mockito.verify(userRepository, Mockito.atMost(1)).getReferenceById(2L);
    }

    @Test
    void getItemDtoWithCorrectId() {
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item1);
        Mockito.when(bookingRepository.getAllBookingsByOwner_IdAndItem_Id(1L, 1L))
                .thenReturn(List.of(booking1));
        Mockito.when(commentRepository.findAllByItem_Id(1L))
                .thenReturn(new ArrayList<>());
        ItemDto itemDto = ItemMapper.mapToItemDto(item1, null, null, new ArrayList<>());

        assertThat(itemDto, equalTo(itemService.getItemDtoById(1L, 1L)));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).getReferenceById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsByOwner_IdAndItem_Id(1L, 1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByItem_Id(1L);
    }

    @Test
    void getItemDtoWithNotExistingId() {
        Mockito.when(itemRepository.findById(100L))
                .thenThrow(new ItemDoesNotExistException("Вещи с таким id не существует"));

        ItemDoesNotExistException itemDoesNotExistException = assertThrows(ItemDoesNotExistException.class,
                () -> itemService.getItemDtoById(100L, 1L));

        assertEquals(itemDoesNotExistException.getMessage(), "Вещи с таким id не существует");

        Mockito.verify(itemRepository, Mockito.times(1)).findById(100L);
    }

    @Test
    void getItemDtoWithSortedBookings() {
        Booking futureBooking1 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user2)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .status(BookingStatus.WAITING)
                .build();

        Booking futureBooking2 = Booking.builder()
                .id(3L)
                .item(item1)
                .booker(user2)
                .start(now.plusDays(4L))
                .end(now.plusDays(5L))
                .status(BookingStatus.WAITING)
                .build();

        Booking pastBooking1 = Booking.builder()
                .id(4L)
                .item(item1)
                .booker(user2)
                .start(now.minusDays(2L))
                .end(now.minusDays(3L))
                .status(BookingStatus.APPROVED)
                .build();

        Booking pastBooking2 = Booking.builder()
                .id(5L)
                .item(item1)
                .booker(user2)
                .start(now.minusDays(4L))
                .end(now.minusDays(5L))
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item1);
        Mockito.when(bookingRepository.getAllBookingsByOwner_IdAndItem_Id(1L, 1L))
                .thenReturn(List.of(pastBooking2, pastBooking1, futureBooking1, futureBooking2));
        Mockito.when(commentRepository.findAllByItem_Id(1L))
                .thenReturn(new ArrayList<>());
        ItemDto itemDto = ItemMapper.mapToItemDto(item1,
                BookingMapper.mapToBookingDto(pastBooking1),
                BookingMapper.mapToBookingDto(futureBooking1),
                new ArrayList<>());

        assertThat(itemDto, equalTo(itemService.getItemDtoById(1L, 1L)));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).getReferenceById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsByOwner_IdAndItem_Id(1L, 1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByItem_Id(1L);
    }

    @Test
    void getItemDtoWhenUserIsNotOwnerOfTheItem() {
        Booking futureBooking1 = Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user2)
                .start(now.plusDays(2L))
                .end(now.plusDays(3L))
                .status(BookingStatus.WAITING)
                .build();

        Booking pastBooking1 = Booking.builder()
                .id(4L)
                .item(item1)
                .booker(user2)
                .start(now.minusDays(2L))
                .end(now.minusDays(3L))
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(itemRepository.getReferenceById(1L))
                .thenReturn(item1);
        Mockito.when(bookingRepository.getAllBookingsByOwner_IdAndItem_Id(2L, 1L))
                .thenReturn(List.of(pastBooking1, futureBooking1));
        Mockito.when(commentRepository.findAllByItem_Id(1L))
                .thenReturn(new ArrayList<>());
        ItemDto itemDto = ItemMapper.mapToItemDto(item1, null, null, new ArrayList<>());

        assertThat(itemDto, equalTo(itemService.getItemDtoById(1L, 2L)));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).getReferenceById(1L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsByOwner_IdAndItem_Id(2L, 1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByItem_Id(1L);
    }

    @Test
    void getAllItemsDtoByUser() {
        Item item3 = Item.builder()
                .id(3L)
                .name("Трос")
                .description("Для буксировки")
                .available(true)
                .owner(user2)
                .build();

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findById(2L))
                .thenReturn(Optional.of(item2));
        Mockito.when(itemRepository.getReferenceById(2L))
                .thenReturn(item2);
        Mockito.when(bookingRepository.getAllBookingsByOwner_IdAndItem_Id(2L, 2L))
                .thenReturn(new ArrayList<>());
        Mockito.when(commentRepository.findAllByItem_Id(2L))
                .thenReturn(new ArrayList<>());
        ItemDto itemDto2 = ItemMapper.mapToItemDto(item2, null, null, new ArrayList<>());

        Mockito.when(itemRepository.findById(3L))
                .thenReturn(Optional.of(item3));
        Mockito.when(itemRepository.getReferenceById(3L))
                .thenReturn(item3);
        Mockito.when(bookingRepository.getAllBookingsByOwner_IdAndItem_Id(2L, 3L))
                .thenReturn(new ArrayList<>());
        Mockito.when(commentRepository.findAllByItem_Id(3L))
                .thenReturn(new ArrayList<>());
        ItemDto itemDto3 = ItemMapper.mapToItemDto(item3, null, null, new ArrayList<>());

        Page<Item> pagedList = new PageImpl(List.of(item2, item3));
        Mockito.when(itemRepository.findAllByOwner_Id(2L, getPage(0, 100)))
                .thenReturn(pagedList);

        assertThat(List.of(itemDto2, itemDto3), equalTo(itemService.getAllItemsDtoByUser(2L, 0, 100)));

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(itemRepository, Mockito.never()).findById(2L);
        Mockito.verify(itemRepository, Mockito.times(1)).getReferenceById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getAllBookingsByOwner_IdAndItem_Id(2L, 2L);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByItem_Id(2L);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByOwner_Id(2L, getPage(0, 100));
    }

    @Test
    void getAllItemsDtoByUserWithIncorrectPaginationParams() {
        Item item3 = Item.builder()
                .id(3L)
                .name("Трос")
                .description("Для буксировки")
                .available(true)
                .owner(user2)
                .build();

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        IncorrectPaginationException incorrectPaginationException = assertThrows(IncorrectPaginationException.class,
                () -> itemService.getAllItemsDtoByUser(2L, -1, 100));

        assertEquals(incorrectPaginationException.getMessage(), "Введены некорректные параметры для пагинации");

        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
        Mockito.verify(itemRepository, Mockito.never()).findAllByOwner_Id(2L, getPage(-1, 100));
    }

    @Test
    void searchItems() {
        Mockito.when(itemService.searchItems("шлифо", null, null))
                .thenReturn(List.of(item1));

        List<Item> foundItems = itemService.searchItems("шлифо", null, null);
        assertThat(foundItems, equalTo(List.of(item1)));
        Mockito.verify(itemRepository, Mockito.times(1)).searchItems("шлифо");
    }

    @Test
    void searchItemsWithPagination() {
        Page<Item> pagedList = new PageImpl(List.of(item1));

        Mockito.when(itemRepository.searchItems("шлифо", getPage(0, 5)))
                .thenReturn(pagedList);

        List<Item> foundItems = itemService.searchItems("шлифо", 0, 5);
        assertThat(foundItems, equalTo(List.of(item1)));
        Mockito.verify(itemRepository, Mockito.times(1))
                .searchItems("шлифо", getPage(0, 5));
    }

    @Test
    void searchItemsWithPaginationEmptyPage() {
        Page<Item> pagedList = new PageImpl(new ArrayList<>());

        Mockito.when(itemRepository.searchItems("шлифо", getPage(2, 5)))
                .thenReturn(pagedList);

        List<Item> foundItems = itemService.searchItems("шлифо", 2, 5);
        assertThat(foundItems, equalTo(new ArrayList<>()));
        Mockito.verify(itemRepository, Mockito.times(1))
                .searchItems("шлифо", getPage(2, 5));
    }

    @Test
    void searchItemsWithNoItemMatch() {
        Mockito.when(itemService.searchItems("автомобиль", null, null))
                .thenReturn(new ArrayList<>());

        List<Item> foundItems = itemService.searchItems("автомобиль", null, null);
        assertThat(foundItems, equalTo(new ArrayList<>()));
        Mockito.verify(itemRepository, Mockito.times(1)).searchItems("автомобиль");
    }

    @Test
    void postComment() {
        booking1.setStart(now.minusHours(2L));
        booking1.setEnd(now.minusHours(1L));

        Mockito.when(userRepository.getReferenceById(2L))
                .thenReturn(user2);
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(bookingRepository.findAllBookingsByItem_Id(1L))
                .thenReturn(List.of(booking1));
        Mockito.when(commentRepository.save(comment1))
                .thenReturn(comment1);

        CommentDto postedComment = itemService.postComment(1L, comment1, 2L);
        commentDto.setCreated(postedComment.getCreated());

        Mockito.when(commentRepository.save(comment2))
                .thenReturn(comment2);
        CommentDto postedComment2 = itemService.postComment(1L, comment2, 2L);
        commentDto2.setCreated(postedComment2.getCreated());

        commentDtos.get(1).setCreated(postedComment2.getCreated());
        assertThat(postedComment, equalTo(commentDto));
        assertThat(postedComment2, equalTo(commentDtos.get(1)));

        Mockito.verify(userRepository, Mockito.times(2)).getReferenceById(2L);
        Mockito.verify(bookingRepository, Mockito.times(2)).findAllBookingsByItem_Id(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment1);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment2);
    }

    @Test
    void postCommentByUserWithNoRightsToPost() {
        Mockito.when(userRepository.getReferenceById(2L))
                .thenReturn(user2);
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(bookingRepository.findAllBookingsByItem_Id(1L))
                .thenReturn(List.of(booking1));
        Mockito.when(commentRepository.save(comment1))
                .thenThrow(new PostCommentProhibitedException("Вещь с id = " + item1.getId() +
                        " не была в аренде у пользователя с id = " + user2.getId() +
                        " либо аренда еще не завершилась."));

        PostCommentProhibitedException postCommentProhibitedException = assertThrows(
                PostCommentProhibitedException.class, () -> itemService.postComment(1L, comment1, 2L));

        assertThat(postCommentProhibitedException.getMessage(), equalTo("Вещь с id = " + item1.getId() +
                " не была в аренде у пользователя с id = " + user2.getId() +
                " либо аренда еще не завершилась."));

        Mockito.verify(userRepository, Mockito.times(1)).getReferenceById(2L);
        Mockito.verify(bookingRepository, Mockito.times(1)).findAllBookingsByItem_Id(1L);
        Mockito.verify(commentRepository, Mockito.never()).save(comment1);
    }

    PageRequest getPage(Integer from, Integer size) {
        if (from != null && size != null) {
            return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
        }
        return null;
    }
}*/
