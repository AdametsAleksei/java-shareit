package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    public void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    public void genericException_thenReturnInternalServerError() {
        Exception exception = new Exception("Internal server error");
        ErrorResponse response = errorHandler.handleException(exception);
        assertThat(response.getMessage()).isEqualTo("Internal server error");
    }

    @Test
    public void genericNotFoundException_thenReturnNotFound() {
        NotFoundException exception = new NotFoundException("Resource not found");
        ErrorResponse response = errorHandler.handleException(exception);
        assertThat(response.getMessage()).isEqualTo("Resource not found");
    }
}