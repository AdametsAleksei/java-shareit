package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserFullDto getUserById(Long userId) {
        return userMapper.toUserFullDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь c ID - " + userId + " не найден")));
    }

    @Transactional
    @Override
    public UserFullDto saveUser(UserCreateDto user) {
        validateEmail(user.getEmail());
        return userMapper.toUserFullDto(
                userRepository.save(userMapper.toUserFromCreateDto(user)));
    }

    @Transactional
    @Override
    public UserFullDto updateUser(UserUpdateDto user) {
        validateEmail(user.getEmail());
        UserFullDto oldUser = getUserById(user.getId());
        if (Objects.isNull(user.getName())) {
            user.setName(oldUser.getName());
        }
        if (Objects.isNull(user.getEmail())) {
            user.setEmail(oldUser.getEmail());
        }
        return userMapper.toUserFullDto(
                userRepository.save(userMapper.toUserFromUpdateDto(user)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("User email is already in use");
        }
    }

}
