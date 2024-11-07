package ru.practicum.shareit.item;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    ItemCreateDto itemCreate;
    ItemUpdateDto itemUpdate;
    ArgumentCaptor<ItemCreateDto> itemCreateCaptor;
    ArgumentCaptor<ItemUpdateDto> itemUpdateCaptor;
    ItemDto itemDone;
    String name = "ITEM";
    String description = "DESCRIPTION";
    Boolean available = true;
    Long itemId = 1L;
    long ownerId = 99L;

    @BeforeEach
    void setUp() {
        itemCreateCaptor = ArgumentCaptor.forClass(ItemCreateDto.class);
        itemUpdateCaptor = ArgumentCaptor.forClass(ItemUpdateDto.class);
        itemCreate = new ItemCreateDto();
        itemCreate.setName(name);
        itemCreate.setDescription(description);
        itemCreate.setAvailable(available);

        itemUpdate = new ItemUpdateDto();

        itemDone = ItemDto.builder()
                .id(1L)
                .name(name)
                .description(description)
                .available(available)
                .ownerId(ownerId)
                .build();
    }

    @SneakyThrows
    @Test
    void createItem() {
        when(itemService.createItem(any(ItemCreateDto.class), anyLong())).thenReturn(itemDone);
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemCreate))
                        .header(SHARER_USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDone)))
                .andExpect(jsonPath("$.id", is(1)));
        verify(itemService, times(1)).createItem(itemCreateCaptor.capture(), anyLong());
        ItemCreateDto capturedDto = itemCreateCaptor.getValue();
        assertThat(capturedDto.getName()).isEqualTo(name);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        itemUpdate.setId(10L);
        itemUpdate.setName("NEW_NAME");
        itemDone.setId(10L);
        itemDone.setName("NEW_NAME");
        when(itemService.updateItem(any(ItemUpdateDto.class))).thenReturn(itemDone);
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", ownerId)
                        .header(SHARER_USER_ID, ownerId)
                        .content(mapper.writeValueAsString(itemUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDone)))
                .andExpect(jsonPath("$.id", is(10)));
        verify(itemService, times(1)).updateItem(itemUpdateCaptor.capture());
        ItemUpdateDto capturedDto = itemUpdateCaptor.getValue();
        assertThat(capturedDto.getName()).isEqualTo("NEW_NAME");
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItemById(itemId, ownerId)).thenReturn(itemDone);
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(SHARER_USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDone)))
                .andExpect(jsonPath("$.id", is(1)));
        verify(itemService, times(1)).getItemById(itemId, ownerId);
    }

    @SneakyThrows
    @Test
    void addComment() {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .authorId(ownerId)
                .itemId(itemId)
                .text("OK")
                .build();
        ArgumentCaptor<CommentCreateDto> commentCreateCaptor = ArgumentCaptor.forClass(CommentCreateDto.class);
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("OK")
                .build();
        when(itemService.addComment(any(CommentCreateDto.class))).thenReturn(commentDto);
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(SHARER_USER_ID, ownerId)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)))
                .andExpect(jsonPath("$.id", is(1)));
        verify(itemService, times(1)).addComment(commentCreateCaptor.capture());
        CommentCreateDto capturedDto = commentCreateCaptor.getValue();
        assertThat(capturedDto.getAuthorId()).isEqualTo(ownerId);
    }

    @SneakyThrows
    @Test
    void getItemsByOwnerId() {
        ItemCommentAndDateDto itemCommentAndDateDto = new ItemCommentAndDateDto();
        itemCommentAndDateDto.setId(12L);
        itemCommentAndDateDto.setAvailable(true);
        itemCommentAndDateDto.setName("ITEM12");

        when(itemService.getItemsByOwnerId(ownerId)).thenReturn(List.of(itemCommentAndDateDto));
        mockMvc.perform(get("/items")
                        .header(SHARER_USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemCommentAndDateDto))))
                .andExpect(jsonPath("$.[0].id", is(12)));
        verify(itemService, times(1)).getItemsByOwnerId(ownerId);
    }

    @SneakyThrows
    @Test
    void searchByName() {
        when(itemService.findByName(name)).thenReturn(List.of(itemDone));
        mockMvc.perform(get("/items/search")
                    .header(SHARER_USER_ID, ownerId)
                    .param("text", name)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDone))))
                .andExpect(jsonPath("$.[0].id", is(1)));
        verify(itemService, times(1)).findByName(name);
    }
}