package com.sevenb.task.api.exceptions;

public class EntityNotFoundException extends ServiceException {

    public EntityNotFoundException(final String message, final int errorCode) {
        super(message, errorCode);
    }
}
