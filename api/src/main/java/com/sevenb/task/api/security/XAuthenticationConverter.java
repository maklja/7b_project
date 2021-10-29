package com.sevenb.task.api.security;

import com.sevenb.task.api.security.authentication.XAuthentication;
import com.sevenb.task.api.security.principal.UserPrincipal;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class XAuthenticationConverter implements AuthenticationConverter {
    private final String authHeader;

    public XAuthenticationConverter(final Environment env) {
        authHeader = env.getRequiredProperty("security.authentication-header");
    }

    @Override
    public Authentication convert(final HttpServletRequest httpServletRequest) {
        final var authHeaderValue = httpServletRequest.getHeader(authHeader);
        return new XAuthentication(UserPrincipal.builder().username(authHeaderValue).build());
    }
}