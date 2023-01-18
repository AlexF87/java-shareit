package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.handler.exception.IllegalArgumentExceptionCustom;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;
    BookingDto bookingDtoNew;
    User owner;
    User booker;
    Item item;

    @BeforeEach
    private void setUp() {
        bookingDtoNew = BookingDto.builder()
                .bookerId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .itemId(1L)
                .id(1L)
                .build();

        owner = User.builder()
                .id(1L)
                .name("Mark")
                .email("mark@mail.test")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Allan")
                .email("allan@mail.test")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Book")
                .description("Spring, java, kotlin")
                .owner(owner)
                .available(true)
                .build();

    }

    @Autowired
    MockMvc mockMvc;

    @SneakyThrows
    @Test
    void createBooking() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .id(1L)
                .start(bookingDtoNew.getStart())
                .end(bookingDtoNew.getEnd())
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();
        when(bookingService.create(any(), any())).thenReturn(bookingDtoInfo);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoNew)))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDtoInfo), result);
    }

    @SneakyThrows
    @Test
    void approve() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .id(1L)
                .start(bookingDtoNew.getStart())
                .end(bookingDtoNew.getEnd())
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();
        when(bookingService.approve(any(), any(), any())).thenReturn(bookingDtoInfo);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingDtoInfo.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDtoInfo), result);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .id(1L)
                .start(bookingDtoNew.getStart())
                .end(bookingDtoNew.getEnd())
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();
        when(bookingService.getBookingById(any(), any())).thenReturn(bookingDtoInfo);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDtoInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId()))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoInfo)));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUserId() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .id(1L)
                .start(bookingDtoNew.getStart())
                .end(bookingDtoNew.getEnd())
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();
        when(bookingService.getAllBookingsByUserId(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoInfo));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDtoInfo))));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUserId_whenStatusNotExists_theThrow() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .id(1L)
                .start(bookingDtoNew.getStart())
                .end(bookingDtoNew.getEnd())
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();
        when(bookingService.getAllBookingsByUserId(any(), any(), anyInt(), anyInt()))
                .thenThrow(IllegalArgumentExceptionCustom.class);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "Not status")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());

    }

    @Test
    void getAllBookingsByUserId_whenFromNotPositive_thenThrowException() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUserId_whenSizeNotPositive_thenThrowException() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "-10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwnerId() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .id(1L)
                .start(bookingDtoNew.getStart())
                .end(bookingDtoNew.getEnd())
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();
        when(bookingService.getAllBookingsByOwnerId(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoInfo));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDtoInfo))));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwnerId_whenFromNotPositive_thenThrowException() {

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwnerId_whenSizeNotPositive_thenThrowException() {

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "-10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError());
    }
}