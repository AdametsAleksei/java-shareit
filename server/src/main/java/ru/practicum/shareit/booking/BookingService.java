package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(BookingState state, Long userId);

    List<BookingDto> getOwnerItemsBookings(BookingState state, Long userId);
}
