package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentCreateDto {
    private Long id;
    private String text;
    private Long itemId;
    private Long authorId;
    private LocalDateTime created;
}