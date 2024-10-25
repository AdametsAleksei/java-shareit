package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {

    public Booking toBookingFromCreateDto(BookingCreateDto bookingCreateDto, Item item, User booker) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}