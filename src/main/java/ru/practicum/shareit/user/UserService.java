package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {

    UserDto getUserById(Long userId);

    UserDto createUser(UserCreateDto user);

    UserDto updateUser(UserUpdateDto user);

    void deleteUser(Long userId);

}
