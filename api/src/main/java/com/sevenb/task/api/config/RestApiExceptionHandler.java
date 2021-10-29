package com.sevenb.task.api.config;

import com.sevenb.task.api.exceptions.EntityNotFoundException;
import com.sevenb.task.api.exceptions.ErrorCodes;
import com.sevenb.task.api.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFound(final EntityNotFoundException ex, final WebRequest request) {
        final var errorResponse = ErrorResponse
                .builder()
                .httpCode(HttpStatus.NOT_FOUND.value())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    protected ResponseEntity<Object> handleForbidden(final AccessDeniedException ex, final WebRequest request) {
        final var errorResponse = ErrorResponse
                .builder()
                .httpCode(HttpStatus.FORBIDDEN.value())
                .errorCode(ErrorCodes.FORBIDDEN.getCode())
                .message(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}