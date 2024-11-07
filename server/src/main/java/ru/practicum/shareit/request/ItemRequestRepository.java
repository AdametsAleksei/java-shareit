package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query(value = "SELECT IR FROM ItemRequest AS IR " +
            "JOIN IR.requester AS USER " +
            "WHERE USER.id != :userId")
    List<ItemRequest> getItemRequestsAll(@Param("userId")long userId);

    List<ItemRequestFullDto> getItemRequestsByRequesterId(long userId);
}
