package ru.practicum.shareit.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
public class UserFullDto {
    private Long id;
    private String name;
    private String email;
}
