package com.sevenb.task.infrastructure.security.permission.handlers;

import org.springframework.security.core.Authentication;

import java.io.Serializable;

public interface AccessHandler {
    boolean handle(Authentication authentication, Serializable entityId, String permission);

    boolean canHandle(String resourceType);
}
