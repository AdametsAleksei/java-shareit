package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        ItemDto itemDto = itemMapper.toItemFullDto(item);
        itemDto.setComments(commentMapper.toCommentDtoList(comments));

        if (item.getOwner().getId() == userId) {
            Booking lastBooking = bookingRepository.findFirstByItemAndEndBeforeOrderByEndDesc(
                    item, LocalDateTime.now());
            if (lastBooking != null && !lastBooking.getBooker().getId().equals(item.getOwner().getId())) {
                itemDto.setLastBooking(lastBooking.getEnd());
            }
            Booking nextBooking = bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(
                    item, LocalDateTime.now());
            if (nextBooking != null && !nextBooking.getBooker().getId().equals(item.getOwner().getId())) {
                itemDto.setNextBooking(nextBooking.getStart());
            }
        }
        return itemDto;
    }

    @Override
    public Collection<ItemCommentAndDateDto> getItemsByOwnerId(Long ownerId) {
        checkUserExist(ownerId);
        Collection<ItemCommentAndDateDto> itemsByOwner = new ArrayList<>();

        itemRepository.getItemsByOwnerId(ownerId).stream().forEach(item -> {
            ItemCommentAndDateDto itemOwnerDto = itemMapper.toItemCommentAndDateDtoFromItem(item);

            Booking lastBooking = bookingRepository.findFirstByItemAndEndBeforeOrderByEndDesc(
                    item, LocalDateTime.now());
            if (lastBooking != null) {
                itemOwnerDto.setLastBooking(lastBooking.getEnd());
            }

            Booking nextBooking = bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(
                    item, LocalDateTime.now());
            if (nextBooking != null) {
                itemOwnerDto.setNextBooking(nextBooking.getStart());
            }

            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            List<CommentDto> commentDtos = comments.stream()
                    .map(commentMapper::toCommentDto)
                    .collect(Collectors.toList());
            itemOwnerDto.setComments(commentDtos);

            itemsByOwner.add(itemOwnerDto);
        });

        return itemsByOwner;
    }

    @Override
    public ItemDto createItem(ItemCreateDto item, Long userId) {
        User owner = checkUserExist(userId);
        ItemDto itemDto = itemMapper.toItemFullDto(itemRepository.save(
                itemMapper.toItemFromCreateDto(item, owner)));

        itemDto.setOwnerId(owner.getId());
        return itemDto;
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto item) {
        Long ownerId = item.getOwnerId();
        ItemDto oldItem = getItemById(item.getId(), ownerId);
        User owner = checkUserExist(ownerId);
        if (Objects.isNull(item.getName())) {
            item.setName(oldItem.getName());
        }
        if (Objects.isNull(item.getDescription())) {
            item.setDescription(oldItem.getDescription());
        }
        if (Objects.isNull(item.getAvailable())) {
            item.setAvailable(oldItem.getAvailable());
        }
        return itemMapper.toItemFullDto(itemRepository.save(
                itemMapper.toItemFromUpdateDto(item, owner)));
    }

    @Override
    public Collection<ItemDto> findByName(String name) {
        if (name.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<ItemDto> itemsBySearch = new ArrayList<>();
        itemRepository.findByNameIgnoreCase(name).stream()
                .peek(itemMapper::toItemFullDto)
                .filter(Item::getAvailable)
                .forEach(item -> itemsBySearch.add(itemMapper.toItemFullDto(item)));
        return itemsBySearch;
    }

    @Override
    public CommentDto addComment(CommentCreateDto commentCreateDto) {
        User author = checkUserExist(commentCreateDto.getAuthorId());
        Item item = itemRepository.findById(commentCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id "
                        + commentCreateDto.getItemId() + " not found"));

        boolean hasBooked = bookingRepository.
                existsByItemIdAndBookerIdAndEndBefore(item.getId(), author.getId(), LocalDateTime.now());
        if (!hasBooked) {
            throw new ValidationException("User has not rented this item or rental is not completed");
        }

        Comment comment = Comment.builder()
                .text(commentCreateDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User checkUserExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }
}
