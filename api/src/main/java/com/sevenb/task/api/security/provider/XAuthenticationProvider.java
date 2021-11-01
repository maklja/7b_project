package com.sevenb.task.api.security.provider;

import com.sevenb.task.api.security.authentication.XAuthentication;
import com.sevenb.task.api.security.exceptions.MissingUsernameException;
import com.sevenb.task.api.security.principal.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class XAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final var principal = (UserPrincipal) authentication.getPrincipal();

        if (StringUtils.isBlank(principal.getUsername())) {
            throw new MissingUsernameException();
        }

        authentication.setAuthenticated(true);

        return authentication;
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return XAuthentication.class.equals(aClass);
    }
}
