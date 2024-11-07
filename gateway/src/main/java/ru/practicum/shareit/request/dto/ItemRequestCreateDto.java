package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestCreateDto {
    @NotEmpty
    private String description;
    private LocalDateTime created;
    private Long requesterId;
}
