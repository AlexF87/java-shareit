package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserService service;
    @Autowired
    MockMvc mockMvc;

    @Test
    void getUsers() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();
        when(service.getUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));
    }

    @Test
    void deleteUserById() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();

        mockMvc.perform(delete("/users/{id}", user.getId()))

                .andExpect(status().isOk());
        verify(service, times(1)).deleteUserById(user.getId());
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();
        when(service.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", user.getId()))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value((user.getName())));
    }

    @Test
    void addUser_whenUserValid_thenReturnSavedUser() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();
        when(service.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect((jsonPath("$.email").value(user.getEmail())));
    }

    @Test
    void addUser_whenUserNotValid_thenReturn400() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testmailru")
                .build();

        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class));

    }

    @Test
    void updateUser() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testmailru")
                .build();
        when(service.updateUser(user.getId(), user)).thenReturn(user);

        mockMvc.perform(patch("/users/{id}", user.getId())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect((jsonPath("$.email").value(user.getEmail())));
    }
}