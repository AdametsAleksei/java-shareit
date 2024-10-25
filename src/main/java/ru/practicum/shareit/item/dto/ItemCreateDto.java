package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ItemCreateDto {
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
}
