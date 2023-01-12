package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    MockMvc mockMvc;

    @Test
    void addItem_whenItemValid_thenReturnSavedItem() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Ручка шариковая")
                .description("No comment")
                .build();
        when(itemService.addItem(any(), any())).thenReturn(item);

        mockMvc.perform(post("/items")

                        .content(new ObjectMapper().writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));

    }

    @Test
    void getAllItems_whenParamFromAndSizePositive_thenReturnResult() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Ручка шариковая")
                .description("No comment")
                .build();
        when(itemService.getAllItems(any(), anyInt(), anyInt())).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "1"))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(item))));
    }

    @Test
    void getItem() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Ручка шариковая")
                .description("No comment")
                .build();
        when(itemService.getItem(any(), any())).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath(("$.name")).value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @Test
    void searchItem() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Ручка шариковая")
                .description("No comment")
                .build();
        when(itemService.searchItem(any(), anyInt(), anyInt())).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "Ручка")
                        .param("from", "1")
                        .param("size", "1"))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(item))));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Ручка шариковая")
                .description("No comment")
                .build();
        when(itemService.updateItem(any(), any(), any())).thenReturn(item);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath(("$.name")).value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @Test
    void createComment_whenCommentValid_thenReturnSavedComment() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Ручка шариковая")
                .description("No comment")
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(2L)
                .authorName("Tomas")
                .text("Text")
                .build();
        when(itemService.createComment(any(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    void createComment_whenCommentEmpty_thenReturn400() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("Ручка шариковая")
                .description("No comment")
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(2L)
                .authorName("Tomas")
                .text("")
                .build();
        when(itemService.createComment(any(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class));

    }
}