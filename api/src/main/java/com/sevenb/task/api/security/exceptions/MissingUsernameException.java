package com.sevenb.task.api.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class MissingUsernameException extends AuthenticationException {
    public MissingUsernameException() {
        super("Authentication header does not contain username");
    }
}
