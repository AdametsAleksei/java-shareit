package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    ItemRequestCreateDto itemRequestCreate;
    ArgumentCaptor<ItemRequestCreateDto> itemRequestCreateCaptor;
    ItemRequestFullDto itemRequestDone;
    ItemRequestDtoWithItems itemRequestWithItems;
    String description = "ITEM_REQUEST";
    Long itemRequestId = 23L;
    long userId = 99L;
    long itemId = 121;

    @BeforeEach
    void setUp() {
        itemRequestCreateCaptor = ArgumentCaptor.forClass(ItemRequestCreateDto.class);
        itemRequestCreate = new ItemRequestCreateDto();
        itemRequestCreate.setDescription(description);

        User user = User.builder()
                .id(userId)
                .name("Aleksei")
                .email("ya@ya.ru")
                .build();

        LocalDateTime time = LocalDateTime.of(2024, 11, 2, 12,21);

        itemRequestDone = ItemRequestFullDto.builder()
                .id(itemRequestId)
                .requester(user)
                .created(time)
                .description(description)
                .build();

        ItemForItemRequestDto item = ItemForItemRequestDto.builder()
                .id(itemId)
                .name("Aleksei")
                .ownerId(userId)
                .build();

        itemRequestWithItems = ItemRequestDtoWithItems.builder()
                .id(itemRequestId)
                .requesterId(userId)
                .created(time)
                .description(description)
                .items(List.of(item))
                .build();
    }

    @SneakyThrows
    @Test
    void createItemRequest() {
        when(itemRequestService.createItemRequest(any(ItemRequestCreateDto.class), anyLong()))
                .thenReturn(itemRequestDone);
        mockMvc.perform(post("/requests")
                        .header(SHARER_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemRequestCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDone)))
                .andExpect(jsonPath("$.id", is(23)));
        verify(itemRequestService, times(1)).createItemRequest(
                itemRequestCreateCaptor.capture(), anyLong());
        ItemRequestCreateDto capturedDto = itemRequestCreateCaptor.getValue();
        assertThat(capturedDto.getDescription()).isEqualTo(itemRequestDone.getDescription());
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        when(itemRequestService.getItemRequestById(itemRequestId))
                .thenReturn(itemRequestWithItems);
        mockMvc.perform(get("/requests/{requestId}", itemRequestId)
                    .header(SHARER_USER_ID, userId)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestWithItems)))
                .andExpect(jsonPath("$.id", is(23)));
        verify(itemRequestService, times(1)).getItemRequestById(itemRequestId);
    }

    @SneakyThrows
    @Test
    void getItemRequestsByRequesterId() {
        when(itemRequestService.getItemRequestsByRequesterId(userId))
                .thenReturn(List.of(itemRequestDone));
        mockMvc.perform(get("/requests")
                        .header(SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDone))))
                .andExpect(jsonPath("$.[0].id", is(23)));
        verify(itemRequestService, times(1)).getItemRequestsByRequesterId(userId);
    }

    @SneakyThrows
    @Test
    void getItemRequestsAllWithoutRequester() {
        User userNew = new User(35L, "Aleksei", "y@ya.ru");
        LocalDateTime time = LocalDateTime.of(2024, 11, 2, 12,21);
        ItemRequestFullDto itemRequest = ItemRequestFullDto.builder()
                .id(48L)
                .requester(userNew)
                .created(time)
                .description(description)
                .build();
        when(itemRequestService.getAllItemRequests(userId))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests/all")
                        .header(SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest))))
                .andExpect(jsonPath("$.[0].id", is(48)));
        verify(itemRequestService, times(1)).getAllItemRequests(userId);
    }
}