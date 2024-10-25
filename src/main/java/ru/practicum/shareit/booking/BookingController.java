package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {
    private static final  String SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                    @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("==> createBooking with user ID : {}", userId);
        BookingDto bookingDto = bookingService.addBooking(bookingCreateDto, userId);
        log.info("<== createBooking finished");
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(SHARER_USER_ID) Long ownerId,
                                     @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("==> approveBooking with booking ID : {}", bookingId);
        BookingDto bookingDto = bookingService.approveBooking(bookingId, ownerId, approved);
        log.info("<== approveBooking finished");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("==> getBooking with booking ID : {}", bookingId);
        BookingDto bookingDto = bookingService.getBookingById(bookingId, userId);
        log.info("<== getBooking finished");
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(name = "state", defaultValue = "ALL") BookingState state,
                                            @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("==> getUserBookings with user ID : {}", userId);
        List<BookingDto> bookingDtoList = bookingService.getUserBookings(state, userId);
        log.info("<== getUserBookings finished");
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(name = "state", defaultValue = "ALL") BookingState state,
                                                  @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("==> getOwnerBookings with owner ID : {}", userId);
        List<BookingDto> bookingDtoList = bookingService.getOwnerItemsBookings(state, userId);
        log.info("<== getOwnerBookings finished");
        return bookingDtoList;
    }
}