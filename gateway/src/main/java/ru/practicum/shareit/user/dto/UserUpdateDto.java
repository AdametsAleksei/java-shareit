package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
