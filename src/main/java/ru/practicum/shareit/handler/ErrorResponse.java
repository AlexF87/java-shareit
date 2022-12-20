package ru.practicum.shareit.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    String error;

    public ErrorResponse(String message) {
        this.error = message;
    }
}
