package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    BookingCreateDto bookingCreate;
    LocalDateTime start = LocalDateTime.of(2024, 11, 7, 12, 10, 11);
    LocalDateTime end = LocalDateTime.of(2024, 11, 7, 12, 13, 11);
    long userId;
    long itemId;
    long ownerId;

    @BeforeEach
    void setUp() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Aleksei");
        userCreateDto.setEmail("ya@ya.ru");
        userId = userService.saveUser(userCreateDto).getId();
        userCreateDto.setName("Owner");
        userCreateDto.setEmail("neew@ya.ru");
        ownerId = userService.saveUser(userCreateDto).getId();

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("ITEM");
        itemCreateDto.setDescription("ITEM DESCRIPTION");
        itemCreateDto.setAvailable(true);
        itemId = itemService.createItem(itemCreateDto, ownerId).getId();

        bookingCreate = new BookingCreateDto();
        bookingCreate.setStart(start);
        bookingCreate.setEnd(end);
        bookingCreate.setItemId(itemId);
    }

    @Test
    void addBooking_WithStartTimeNull_ShouldThrowException() {
        bookingCreate.setStart(null);
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingCreate, userId));
    }

    @Test
    void addBooking_WithEndTimeNull_ShouldThrowException() {
        bookingCreate.setEnd(null);
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingCreate, userId));
    }

    @Test
    void addBooking_WithStartAfterEnd_ShouldThrowException() {
        bookingCreate.setStart(end.plusHours(1));
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingCreate, userId));
    }

    @Test
    void addBooking_WithStartEqualsEnd_ShouldThrowException() {
        bookingCreate.setStart(end);
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingCreate, userId));
    }

    @Test
    void addBooking_UserNotExist_ShouldThrowException() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(bookingCreate, 100L));
    }

    @Test
    void addBooking_ItemNotExist_ShouldThrowException() {
        bookingCreate.setItemId(100L);
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(bookingCreate, userId));
    }

    @Test
    void addBooking_ItemOwnerAndUserEquals_ShouldThrowException() {
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingCreate, ownerId));
    }

    @Test
    void addBooking_ItemNotAvailable_ShouldThrowException() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("ITEM");
        itemCreateDto.setDescription("ITEM DESCRIPTION");
        itemCreateDto.setAvailable(false);
        Long itemIdNew = itemService.createItem(itemCreateDto, ownerId).getId();
        bookingCreate.setItemId(itemIdNew);
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingCreate, userId));
    }

    @Test
    void addBooking() {
        BookingDto bookingDto = bookingService.addBooking(bookingCreate, userId);
        assertEquals(bookingCreate.getStart(), bookingDto.getStart());
        assertEquals(bookingCreate.getEnd(), bookingDto.getEnd());
        assertEquals(bookingCreate.getItemId(), bookingDto.getItem().getId());
    }

    @Test
    void approveBooking_WithWrongId_ShouldThrowException() {
        Long bookingId = bookingService.addBooking(bookingCreate, userId).getId();
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(bookingId + 1, ownerId, true));
    }

    @Test
    void approveBooking_WithWrongOwnerId_ShouldThrowException() {
        Long bookingId = bookingService.addBooking(bookingCreate, userId).getId();
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(bookingId, ownerId + 1, true));
    }

    @Test
    void approveBooking_Approved() {
        Long bookingId = bookingService.addBooking(bookingCreate, userId).getId();
        BookingDto bookingDto = bookingService.approveBooking(bookingId, ownerId, true);
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void approveBooking_Rejected() {
        Long bookingId = bookingService.addBooking(bookingCreate, userId).getId();
        BookingDto bookingDto = bookingService.approveBooking(bookingId, ownerId, false);
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }

    @Test
    void getBookingById_WithWrongId_ShouldThrowException() {
        Long bookingId = bookingService.addBooking(bookingCreate, userId).getId();
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(bookingId + 1, ownerId));
    }

    @Test
    void getBookingById_WithWrongUserId_ShouldThrowException() {
        Long bookingId = bookingService.addBooking(bookingCreate, userId).getId();
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(bookingId, ownerId + 1));
    }

    @Test
    void getBookingById() {
        Long bookingId = bookingService.addBooking(bookingCreate, userId).getId();
        BookingDto bookingDto = bookingService.getBookingById(bookingId, ownerId);
        assertEquals(bookingCreate.getItemId(), bookingDto.getItem().getId());
        assertEquals(bookingCreate.getStart(), bookingDto.getStart());
        assertEquals(bookingCreate.getEnd(), bookingDto.getEnd());
    }

    @Test
    void getUserBookings_WithWrongUserId_ShouldThrowException() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getUserBookings(BookingState.ALL,1000L));
    }

    @Test
    void getUserBookings() {
        bookingService.addBooking(bookingCreate, userId);
        List<BookingDto> bookings = bookingService.getUserBookings(BookingState.ALL, userId);
        assertEquals(1, bookings.size());
        assertEquals(bookingCreate.getItemId(), bookings.getFirst().getItem().getId());
    }

    @Test
    void getOwnerItemsBookings_WithWrongOwnerId_ShouldThrowException() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getOwnerItemsBookings(BookingState.ALL,1000L));
    }

    @Test
    void getOwnerItemsBookings_StateAll() {
        bookingService.addBooking(bookingCreate, userId);
        List<BookingDto> bookings = bookingService.getOwnerItemsBookings(BookingState.ALL, ownerId);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerItemsBookings_StateCurrent() {
        bookingCreate.setEnd(LocalDateTime.now().plusDays(1));
        bookingService.addBooking(bookingCreate, userId);
        List<BookingDto> bookings = bookingService.getOwnerItemsBookings(BookingState.CURRENT, ownerId);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerItemsBookings_StateFuture() {
        bookingCreate.setStart(LocalDateTime.now().plusDays(1));
        bookingCreate.setEnd(bookingCreate.getStart().plusDays(1));
        bookingService.addBooking(bookingCreate, userId);
        List<BookingDto> bookings = bookingService.getOwnerItemsBookings(BookingState.FUTURE, ownerId);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerItemsBookings_StatePast() {
        bookingService.addBooking(bookingCreate, userId);
        List<BookingDto> bookings = bookingService.getOwnerItemsBookings(BookingState.PAST, ownerId);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerItemsBookings_StatusWaiting() {
        bookingService.addBooking(bookingCreate, userId);
        List<BookingDto> bookings = bookingService.getOwnerItemsBookings(BookingState.WAITING, ownerId);
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerItemsBookings_StatusApproved() {
        Long id = bookingService.addBooking(bookingCreate, userId).getId();
        bookingService.approveBooking(id, ownerId, false);
        List<BookingDto> bookings = bookingService.getOwnerItemsBookings(BookingState.REJECTED, ownerId);
        assertEquals(1, bookings.size());
    }
}