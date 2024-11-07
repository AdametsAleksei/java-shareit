package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {

    UserFullDto getUserById(Long userId);

    UserFullDto saveUser(UserCreateDto user);

    UserFullDto updateUser(UserUpdateDto user);

    void deleteUser(Long userId);

}
