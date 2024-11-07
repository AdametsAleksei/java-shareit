package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class ItemRequestFullDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private User requester;
}
