package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserDto {
    private Long id;
    @NotBlank
    String name;
    @NotNull
    @Email
    String email;
}
