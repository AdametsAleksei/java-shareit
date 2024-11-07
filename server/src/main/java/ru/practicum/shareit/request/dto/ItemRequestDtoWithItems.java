package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class ItemRequestDtoWithItems {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Long requesterId;
    private List<ItemForItemRequestDto> items;
}

