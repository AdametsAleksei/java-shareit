package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Setter
@Getter
public class User {
    private Long id;
    private final String name;
    private final String email;
}
