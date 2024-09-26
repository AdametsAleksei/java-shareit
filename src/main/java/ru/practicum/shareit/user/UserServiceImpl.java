package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserById(Long userId) {
        userRepository.isUserNotExist(userId);
        return userMapper.toUserFullDto(userRepository.getUserById(userId).get());
    }

    @Override
    public UserDto createUser(UserCreateDto user) {
        userRepository.isEmailAlreadyUse(user.getEmail());
        return userMapper.toUserFullDto(
                userRepository.createUser(userMapper.toUserFromCreateDto(user)));
    }

    @Override
    public UserDto updateUser(UserUpdateDto user) {
        userRepository.isUserNotExist(user.getId());
        userRepository.isEmailAlreadyUse(user.getEmail());
        if (Objects.isNull(user.getName())) {
            user.setName(userRepository.getUserById(user.getId()).get().getName());
        } if (Objects.isNull(user.getEmail())) {
            user.setEmail(userRepository.getUserById(user.getId()).get().getEmail());
        }
        return userMapper.toUserFullDto(
                userRepository.updateUser(userMapper.toUserFromUpdateDto(user)));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.isUserNotExist(userId);
        userRepository.deleteUser(userId);
    }
}
