package ru.practicum.shareit.user.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    @Email
    private String email;
}
