package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    public ItemDto postItemDto(ItemDto itemDto, long userId) {
        UserDto userDto = checkAndGetUserDtoIfExists(userId);
        itemDto.setOwner(userDto);
        Item item = ItemMapper.mapToItem(itemDto);
        item.setRequestId(itemDto.getRequestId());
        Item savedItem = itemRepository.save(item);
        itemDto.setId(savedItem.getId());
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto patchItemDto(long itemId, ItemDto itemDto, long userId) {
        checkIfUserAndItemExists(userId, itemId);
        itemDto.setId(itemId);
        Item existingItem = getItem(itemId);
        ItemDto existingItemDto = getItemDtoWithBookingsAndComments(existingItem, userId);

        if (itemDto.getName() == null) {
            itemDto.setName(existingItemDto.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(existingItemDto.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(existingItemDto.getAvailable());
        }
        if (itemDto.getRequestId() == null) {
            itemDto.setRequestId(existingItemDto.getRequestId());
        }
        if (itemDto.getOwner() == null) {
            itemDto.setOwner(existingItemDto.getOwner());
        }
        Item item = ItemMapper.mapToItem(itemDto);
        itemRepository.save(item);

        itemDto.setLastBooking(existingItemDto.getLastBooking());
        itemDto.setNextBooking(existingItemDto.getNextBooking());
        itemDto.setComments(existingItemDto.getComments());

        return itemDto;
    }

    @Override
    public ItemDto getItemDtoById(long itemId, long userId) {
        Item item = getItem(itemId);

        return getItemDtoWithBookingsAndComments(item, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsDtoByUser(long userId, Integer from, Integer size) {
        checkAndGetUserDtoIfExists(userId);

        List<Item> itemsList = new ArrayList<>();
        if (from != null && size != null) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
            Page<Item> pagedList = itemRepository.findAllByOwner_Id(userId, page);
            if (pagedList != null) {
                itemsList = pagedList.getContent();
            }
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
    public List<ItemDtoForSearch> searchItemDto(String text, Integer from, Integer size) {
        List<Item> itemsFoundBySearch = new ArrayList<>();
        if (!text.isBlank()) {
            if (from != null && size != null) {
                PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
                Page<Item> pagedList = itemRepository.searchItems(text, page);
                if (pagedList != null) {
                    itemsFoundBySearch = pagedList.getContent();
                }
            } else {
                itemsFoundBySearch = itemRepository.searchItems(text);
            }
        }

        List<ItemDtoForSearch> itemDtos2FoundBySearch = new ArrayList<>();
        for (Item item : itemsFoundBySearch) {
            itemDtos2FoundBySearch.add(ItemMapper.mapToItemDtoForSearch(item));
        }

        return itemDtos2FoundBySearch;
    }

    @Override
    @Transactional
    public CommentDto postCommentDto(long itemId, CommentDto commentDto, long userId) {
        if (commentDto.getText().isBlank()) {
            throw new EmptyCommentException("Комментарий не может быть пустым.");
        }

        Item item = getItem(itemId);
        User author = userRepository.getReferenceById(userId);
        List<Booking> allBookingsForItem = bookingRepository.findAllBookingsByItem_Id(itemId);
        LocalDateTime now = LocalDateTime.now().plusSeconds(1);
        Comment comment = new Comment();
        for (Booking booking : allBookingsForItem) {
            if (booking.getBooker().getId() == userId && booking.getEnd().isBefore(now)) {
                comment.setText(commentDto.getText());
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

    private UserDto checkAndGetUserDtoIfExists(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Пользователя с таким id не существует"));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional(readOnly = true)
    private List<Item> getAllItemsByOwner(long userId) {
        checkAndGetUserDtoIfExists(userId);
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
        List<BookingDtoResponseForItemDto> ownersBookings = BookingMapper.mapToBookingDtoOutForItemDto(
                bookingRepository.getAllBookingsByOwner_IdAndItem_Id(userId, item.getId()));
        List<BookingDtoResponseForItemDto> pastBookings = new ArrayList<>();
        List<BookingDtoResponseForItemDto> futureBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (BookingDtoResponseForItemDto bookingDto : ownersBookings) {
            if ((bookingDto.getEnd().isBefore(now) && bookingDto.getStatus() != BookingStatus.REJECTED)
                    || (bookingDto.getStart().isBefore(now) && bookingDto.getEnd().isAfter(now)
                    && bookingDto.getStatus() == BookingStatus.APPROVED)) {
                pastBookings.add(bookingDto);
            }
            if (bookingDto.getStart().isAfter(now) && bookingDto.getStatus() != BookingStatus.REJECTED) {
                futureBookings.add(bookingDto);
            }
        }
        BookingDtoResponseForItemDto lastBookingDto = null;
        BookingDtoResponseForItemDto nextBookingDto = null;

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