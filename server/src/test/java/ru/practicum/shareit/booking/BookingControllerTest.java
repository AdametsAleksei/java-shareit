package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    BookingCreateDto bookingCreate;
    ArgumentCaptor<BookingCreateDto> bookingCreateCaptor;
    BookingDto bookingDone;
    LocalDateTime start = LocalDateTime.of(2024, 11, 7, 12, 10, 11);
    LocalDateTime end = LocalDateTime.of(2024, 11, 7, 12, 13, 11);
    Long bookingId = 21L;
    long userId = 99L;
    long itemId = 121;


    @BeforeEach
    void setUp() {
        User booker = User.builder()
                .id(userId)
                .name("Aleksei")
                .email("ya@ya.ru")
                .build();

        bookingCreate = new BookingCreateDto();
        bookingCreate.setStart(start);
        bookingCreate.setEnd(end);
        bookingCreate.setItemId(itemId);

        bookingDone = BookingDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(booker)
                .build();

        bookingCreateCaptor = ArgumentCaptor.forClass(BookingCreateDto.class);
    }

    @SneakyThrows
    @Test
    void createBooking() {
        when(bookingService.addBooking(any(BookingCreateDto.class), anyLong())).thenReturn(bookingDone);
        mockMvc.perform(post("/bookings")
                    .header(SHARER_USER_ID, userId)
                    .content(mapper.writeValueAsString(bookingCreate))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDone)))
                .andExpect(jsonPath("$.id", is(21)));
        verify(bookingService, times(1))
                .addBooking(bookingCreateCaptor.capture(), anyLong());
        BookingCreateDto capturedDto = bookingCreateCaptor.getValue();
        assertThat(capturedDto.getStart()).isEqualTo(bookingDone.getStart());
    }

    @SneakyThrows
    @Test
    void approveBooking() {
        bookingDone.setStatus(BookingStatus.APPROVED);
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDone);
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(SHARER_USER_ID, userId)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDone)))
                .andExpect(jsonPath("$.id", is(21)));
        verify(bookingService, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void getBookingById(){
        when(bookingService.getBookingById(bookingId, userId)).thenReturn(bookingDone);
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                    .header(SHARER_USER_ID, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDone)))
                .andExpect(jsonPath("$.id", is(21)));
        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getUserBookings() {
        when(bookingService.getUserBookings(any(), anyLong())).thenReturn(List.of(bookingDone));
        mockMvc.perform(get("/bookings")
                        .header(SHARER_USER_ID, userId)
                        .param("state", BookingState.ALL.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDone))))
                .andExpect(jsonPath("$.[0].id", is(21)));
        verify(bookingService, times(1)).getUserBookings(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void getOwnerBookings() {
        when(bookingService.getOwnerItemsBookings(any(), anyLong())).thenReturn(List.of(bookingDone));
        mockMvc.perform(get("/bookings/owner")
                        .header(SHARER_USER_ID, userId)
                        .param("state", BookingState.ALL.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDone))))
                .andExpect(jsonPath("$.[0].id", is(21)));
        verify(bookingService, times(1)).getOwnerItemsBookings(any(), anyLong());
    }
}