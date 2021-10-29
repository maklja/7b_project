package com.sevenb.task.api.security.permission;

import com.sevenb.task.api.security.permission.handlers.AccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@RequiredArgsConstructor
public class XPermissionEvaluator implements PermissionEvaluator {
    private final List<AccessHandler> handlers;

    @Override
    public boolean hasPermission(final Authentication authentication,
                                 final Object resource,
                                 final Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(final Authentication authentication,
                                 final Serializable resourceId,
                                 final String resourceType,
                                 final Object permission) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(resourceType))
                .findFirst()
                .map(handler -> handler.handle(authentication, resourceId, permission.toString()))
                .orElse(false);
    }
}
