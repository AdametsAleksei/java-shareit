package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long userId);

    List<Booking> findAllBookersByItemOwnerId(Long userId);

    Booking findFirstByItemAndEndBeforeOrderByEndDesc(Item item, LocalDateTime end);

    Booking findFirstByItemAndStartAfterOrderByStartAsc(Item item, LocalDateTime start);

    Boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);
}