package com.sevenb.task.infrastructure.exceptions;

public class EntityNotFoundException extends ServiceException {

    public EntityNotFoundException(final String message, final int errorCode) {
        super(message, errorCode);
    }
}
