package com.sevenb.task.api.security.principal;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class UserPrincipal {
    private final String username;
}
