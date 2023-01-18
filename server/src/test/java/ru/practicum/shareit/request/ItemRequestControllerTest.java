package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @SneakyThrows
    @Test
    void createRequest_whenValidData_thenReturnRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Book")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.createRequest(any(), any())).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)))
                .andReturn().getResponse().getContentAsString();
        assertEquals(result, objectMapper.writeValueAsString(itemRequestDto));
    }

    @SneakyThrows
    @Test
    void createRequest_whenDescriptionEmpty_thenThrowException() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(null)
                .created(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))

                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).createRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void getItemRequestsByUserId() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Book")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getItemRequestsByUserId(any())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));
        verify(itemRequestService, times(1)).getItemRequestsByUserId(1L);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Book")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getAllRequests(any(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));
        verify(itemRequestService, times(1)).getAllRequests(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenFromIsNegative_thenReturnStatus500() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Book")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getAllRequests(any(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError());
        verify(itemRequestService, never()).getAllRequests(1L, -1, 10);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenSizeIsNegative_thenReturnStatus500() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Book")
                .created(LocalDateTime.now())
                .build();
        when(itemRequestService.getAllRequests(any(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "-10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isInternalServerError());

        verify(itemRequestService, never()).getAllRequests(1L, 0, -10);
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Book")
                .created(LocalDateTime.now().withNano(0))
                .build();
        when(itemRequestService.getRequestById(any(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().toString()));
        verify(itemRequestService, times(1)).getRequestById(itemRequestDto.getId(), 1L);
    }
}