package ru.practicum.shareit.handler.exception;

public class IllegalArgumentExceptionCustom extends RuntimeException {
    public IllegalArgumentExceptionCustom(String message) {
        super(message);
    }
}
