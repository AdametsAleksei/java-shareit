package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    public BookingDto addBooking(BookingCreateDto bookingCreateDto, Long userId) {
        if (bookingCreateDto.getStart() == null || bookingCreateDto.getEnd() == null) {
            throw new ValidationException("Start and end dates must not be null");
        }
        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd())
                || bookingCreateDto.getStart().equals(bookingCreateDto.getEnd())
                || bookingCreateDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Something wrong with start and end date");
        }
        User booker = checkUserExist(userId);
        Item item = checkItemExist(bookingCreateDto.getItemId());
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new ValidationException("Owner can't booking own item");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }

        return bookingMapper.toBookingDto(bookingRepository.save(
                bookingMapper.toBookingFromCreateDto(bookingCreateDto, item, booker)));
    }

    public BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Only owner can approve/reject booking");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("User is not authorized to view this booking");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(BookingState state, Long userId) {
        checkUserExist(userId);
        List<Booking> userBookings = bookingRepository.findAllByBookerId(userId);
        return bookingFilter(userBookings, state);
    }

    @Override
    public List<BookingDto> getOwnerItemsBookings(BookingState state, Long userId) {
        checkUserExist(userId);
        if (itemRepository.getItemsByOwnerId(userId).isEmpty()) return new ArrayList<>();
        List<Booking> userBookings = bookingRepository.findAllBookersByItemOwnerId(userId);
        return bookingFilter(userBookings, state);
    }

    private List<BookingDto> bookingFilter(List<Booking> userBookings, BookingState state) {
        List<Booking> userBookingsTemp = userBookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                break;
            case CURRENT:
                userBookingsTemp = userBookings.stream()
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .toList();
                break;
            case PAST:
                userBookingsTemp = userBookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .toList();
                break;
            case FUTURE:
                userBookingsTemp = userBookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .toList();
                break;
            case WAITING:
                userBookingsTemp = userBookings.stream()
                        .filter(b -> b.getStatus().equals(BookingStatus.WAITING))
                        .toList();
                break;
            case REJECTED:
                userBookingsTemp = userBookings.stream()
                        .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                        .toList();
                break;

        }
        return userBookingsTemp.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    private User checkUserExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Item checkItemExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }
}