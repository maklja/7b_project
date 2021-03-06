package com.sevenb.task.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final int errorCode;

    public ServiceException(final String message, final int errorCode) {
        super(message);

        this.errorCode = errorCode;
    }
}
