package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
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
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item postItem(Item item, long userId) {
        User user = checkAndGetUserIfExists(userId);
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item patchItem(long itemId, Item item, long userId) {
        checkIfUserAndItemExists(userId, itemId);
        item.setId(itemId);
        Item existingItemDto = getItem(itemId);
        if (item.getName() == null) {
            item.setName(existingItemDto.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(existingItemDto.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(existingItemDto.getAvailable());
        }
        if (item.getRequestId() == null) {
            item.setRequestId(existingItemDto.getRequestId());
        }
        if (item.getOwner() == null) {
            item.setOwner(existingItemDto.getOwner());
        }

        return itemRepository.save(item);
    }

    @Override
    public ItemDto getItemDtoById(long itemId, long userId) {
        Item item = getItem(itemId);

        return getItemDtoWithBookingsAndComments(item, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsDtoByUser(long userId, Integer from, Integer size) {
        checkAndGetUserIfExists(userId);
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        List<Item> itemsList;
        if (from != null && size != null) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
            itemsList = itemRepository
                    .findAllByOwner_Id(userId, page)
                    .getContent();
        } else {
            itemsList = itemRepository.findAllByOwner_Id(userId);
        }

        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemsList) {
            itemDtoList.add(getItemDtoWithBookingsAndComments(item, userId));
        }

        itemDtoList.sort((itemDto1, itemDto2) -> {
            if (itemDto1.getId() <= itemDto2.getId()) {
                return -1;
            } else {
                return 1;
            }
        });

        return itemDtoList;
    }

    @Override
    public List<Item> searchItems(String text, Integer from, Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        List<Item> foundBySearch = new ArrayList<>();
        if (!text.isBlank()) {
            if (from != null && size != null) {
                PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
                foundBySearch = itemRepository
                        .searchItems(text, page)
                        .getContent();
            } else {
                foundBySearch = itemRepository.searchItems(text);
            }
        }

        return foundBySearch;
    }

    @Override
    @Transactional
    public CommentDto postComment(long itemId, Comment comment, long userId) {
        if (comment.getText().isBlank()) {
            throw new EmptyCommentException("Комментарий не может быть пустым.");
        }

        Item item = getItem(itemId);
        User author = userRepository.getReferenceById(userId);
        List<Booking> allBookingsForItem = bookingRepository.findAllBookingsByItem_Id(itemId);
        LocalDateTime now = LocalDateTime.now().plusSeconds(1);
        for (Booking booking : allBookingsForItem) {
            if (booking.getBooker().getId() == userId && booking.getEnd().isBefore(now)) {
                comment.setItem(item);
                comment.setAuthor(author);
                comment.setCreated(now);

                Comment savedComment = commentRepository.save(comment);
                return CommentMapper.mapToCommentDto(savedComment);
            }
        }

        throw new PostCommentProhibitedException("Вещь с id = " + itemId + " не была в аренде у пользователя с id = " +
                userId + " либо аренда еще не завершилась.");
    }

    private User checkAndGetUserIfExists(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Пользователя с таким id не существует"));
    }

    @Transactional(readOnly = true)
    private List<Item> getAllItemsByOwner(long userId) {
        checkAndGetUserIfExists(userId);
            return itemRepository.findAllByOwner_Id(userId);
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemDoesNotExistException("Вещи с id = " + itemId + " не существует."));
    }

    private boolean isUserOwnerOfItem(long itemId, long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner().getId() == userId) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    private ItemDto getItemDtoWithBookingsAndComments(Item item, long userId) {
        List<BookingDto> ownersBookings = BookingMapper.mapToBookingDto(
                bookingRepository.getAllBookingsByOwner_IdAndItem_Id(userId, item.getId()));
        List<BookingDto> pastBookings = new ArrayList<>();
        List<BookingDto> futureBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (BookingDto bookingDto : ownersBookings) {
            if ((bookingDto.getEnd().isBefore(now) && bookingDto.getStatus() != BookingStatus.REJECTED)
                    || (bookingDto.getStart().isBefore(now) && bookingDto.getEnd().isAfter(now)
                    && bookingDto.getStatus() == BookingStatus.APPROVED)) {
                pastBookings.add(bookingDto);
            }
            if (bookingDto.getStart().isAfter(now) && bookingDto.getStatus() != BookingStatus.REJECTED) {
                futureBookings.add(bookingDto);
            }
        }
        BookingDto lastBookingDto = null;
        BookingDto nextBookingDto = null;

        if (isUserOwnerOfItem(item.getId(), userId)) {
            if (!pastBookings.isEmpty()) {
                pastBookings.sort((bookingDto1, bookingDto2) -> {
                    if (bookingDto1.getEnd().isBefore(bookingDto2.getEnd())) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
                lastBookingDto = pastBookings.get(pastBookings.size() - 1);
            }
            if (!futureBookings.isEmpty()) {
                futureBookings.sort((bookingDto1, bookingDto2) -> {
                    if (bookingDto1.getStart().isBefore(bookingDto2.getStart())) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
                nextBookingDto = futureBookings.get(0);
            }
        }

        List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());

        return ItemMapper.mapToItemDto(item, lastBookingDto, nextBookingDto, comments);
    }

    private void checkIfUserAndItemExists(long userId, long itemId) {
        List<Item> userItems = getAllItemsByOwner(userId);
        if (userItems.isEmpty()) {
            throw new UserDoesNotExistOrDoesNotHaveAnyItemsException(
                    "Пользователя с указанным id не существует либо пользователь не добавил ни одной вещи.");
        }
        for (Item item : userItems) {
            if (item.getId() == itemId) {
                return;
            }
        }

        throw new ItemDoesNotExistException("У пользователя нет вещи с указанным id.");
    }
}