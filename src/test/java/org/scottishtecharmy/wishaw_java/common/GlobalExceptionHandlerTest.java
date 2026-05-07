package org.scottishtecharmy.wishaw_java.common;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ── IllegalArgumentException ────────────────────────────────────

    @Test
    void handleIllegalArgument_returnsBadRequest() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("User not found"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);
        assertThat(response.getBody()).containsEntry("message", "User not found");
        assertThat(response.getBody()).containsEntry("error", "Bad Request");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleIllegalArgument_customMessage() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("Username already exists"));

        assertThat(response.getBody().get("message")).isEqualTo("Username already exists");
    }

    // ── MethodArgumentNotValidException (Validation) ────────────��───

    @Test
    @SuppressWarnings("unchecked")
    void handleValidation_returnsBadRequestWithFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "username", "username is required"),
                new FieldError("request", "password", "password must be 6 to 100 characters")
        ));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);
        assertThat(response.getBody()).containsEntry("error", "Validation Failed");
        assertThat(response.getBody()).containsKey("timestamp");

        Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
        assertThat(fields).containsEntry("username", "username is required");
        assertThat(fields).containsEntry("password", "password must be 6 to 100 characters");
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleValidation_emptyFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
        assertThat(fields).isEmpty();
    }

    // ── AccessDeniedException ───────────────────────────────────────

    @Test
    void handleAccessDenied_returnsForbidden() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleAccessDenied(new AccessDeniedException("forbidden"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("status", 403);
        assertThat(response.getBody()).containsEntry("message", "Access denied");
        assertThat(response.getBody()).containsEntry("error", "Forbidden");
    }

    // ── General Exception ───────────────────────────────────────────

    @Test
    void handleGeneral_returnsInternalServerError() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleGeneral(new RuntimeException("something broke"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("status", 500);
        assertThat(response.getBody()).containsEntry("message", "An unexpected error occurred");
        assertThat(response.getBody()).containsEntry("error", "Internal Server Error");
    }

    @Test
    void handleGeneral_doesNotLeakExceptionMessage() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleGeneral(new NullPointerException("sensitive detail"));

        // Should return generic message, not the actual exception message
        assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred");
    }
}

