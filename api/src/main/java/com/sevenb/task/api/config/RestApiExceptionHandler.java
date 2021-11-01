package com.sevenb.task.api.config;

import com.sevenb.task.api.response.ErrorResponse;
import com.sevenb.task.infrastructure.exceptions.EntityNotFoundException;
import com.sevenb.task.infrastructure.exceptions.ErrorCodes;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static ErrorResponse createBadRequestErrorResponse(final String message) {
        return ErrorResponse
                .builder()
                .httpCode(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCodes.INVALID_INPUT.getCode())
                .message(message)
                .build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {
        return handleExceptionInternal(ex, createBadRequestErrorResponse(ex.getMessage()), headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex,
                                                        final HttpHeaders headers,
                                                        final HttpStatus status,
                                                        final WebRequest request) {
        return handleExceptionInternal(ex, createBadRequestErrorResponse(ex.getMessage()), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(final EntityNotFoundException ex, final WebRequest request) {
        final var errorResponse = ErrorResponse
                .builder()
                .httpCode(HttpStatus.NOT_FOUND.value())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<Object> handleForbidden(final AccessDeniedException ex, final WebRequest request) {
        final var errorResponse = ErrorResponse
                .builder()
                .httpCode(HttpStatus.FORBIDDEN.value())
                .errorCode(ErrorCodes.FORBIDDEN.getCode())
                .message(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
        return handleExceptionInternal(ex, createBadRequestErrorResponse(ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}