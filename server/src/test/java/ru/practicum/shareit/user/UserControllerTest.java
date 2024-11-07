package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    UserCreateDto userCreateDto;
    UserFullDto userDone;
    UserUpdateDto userUpdateDto;
    ArgumentCaptor<UserCreateDto> userCreateDtoCaptor;
    ArgumentCaptor<UserUpdateDto> userUpdateDtoCaptor;
    Long userId = 1L;
    String name = "Aleksei";
    String email = "aleksei@ya.ru";

    @BeforeEach
    void setUp() {
        userCreateDtoCaptor = ArgumentCaptor.forClass(UserCreateDto.class);
        userUpdateDtoCaptor = ArgumentCaptor.forClass(UserUpdateDto.class);

        userCreateDto = new UserCreateDto();
        userCreateDto.setName(name);
        userCreateDto.setEmail(email);

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(userId);
        userUpdateDto.setName("Aleksei 2.0");
        userUpdateDto.setEmail(email);

        userDone = UserFullDto.builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();
    }

    @SneakyThrows
    @Test
    void saveUser() {
        when(userService.saveUser(any(UserCreateDto.class))).thenReturn(userDone);
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDone)))
                .andExpect(jsonPath("$.id", is(1)));
        verify(userService, times(1)).saveUser(userCreateDtoCaptor.capture());
        UserCreateDto capturedDto = userCreateDtoCaptor.getValue();
        assertThat(capturedDto.getEmail()).isEqualTo(email);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        userDone.setName("Aleksei 2.0");
        when(userService.updateUser(any(UserUpdateDto.class))).thenReturn(userDone);
        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDone)))
                .andExpect(jsonPath("$.id", is(1)));
        verify(userService, times(1)).updateUser(userUpdateDtoCaptor.capture());
        UserUpdateDto capturedDto = userUpdateDtoCaptor.getValue();
        assertThat(capturedDto.getName()).isEqualTo("Aleksei 2.0");
    }

    @SneakyThrows
    @Test
    void getUserById() {
        when(userService.getUserById(userId)).thenReturn(userDone);
        mockMvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDone)))
                .andExpect(jsonPath("$.id", is(1)));
        verify(userService, times(1)).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void deleteUserById() {
        mockMvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(userId);
    }

}