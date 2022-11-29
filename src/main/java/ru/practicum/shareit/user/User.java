package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    @NotBlank
    @NotNull
    @Email
    private String email;
}
