package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {
    private static final  String SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(SHARER_USER_ID) long userId,
                                                @RequestBody @Valid BookingCreateDto booking) {
        return bookingClient.createBooking(booking, userId);
    }

    @PatchMapping(("/{bookingId}"))
    public ResponseEntity<Object> approveBooking(@RequestHeader(SHARER_USER_ID) Long ownerId,
                                           @PathVariable("bookingId") Long bookingId,
                                           @RequestParam Boolean approved) {
        return bookingClient.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(SHARER_USER_ID) long userId,
                                                 @PathVariable("bookingId") long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(SHARER_USER_ID) long userId,
            @RequestParam(defaultValue = "ALL", name = "state") BookingState state) {
        return bookingClient.getUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(SHARER_USER_ID) long ownerId,
            @RequestParam(defaultValue = "ALL", name = "state") BookingState state) {
        return bookingClient.getOwnerBookings(state, ownerId);
    }

}