package com.sevenb.task.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenb.task.api.exceptions.ErrorCodes;
import com.sevenb.task.api.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class XAuthenticationFilter extends AuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public XAuthenticationFilter(final Collection<String> unsecuredRoutes,
                                 final AuthenticationManager authenticationManager,
                                 final AuthenticationConverter authenticationConverter) {
        super(authenticationManager, authenticationConverter);

        this.setSuccessHandler(this::onAuthenticationSuccess);
        this.setFailureHandler(this::onAuthenticationFailure);
        this.setRequestMatcher(new RequestMatcherProxy(unsecuredRoutes));
    }

    private void onAuthenticationSuccess(final HttpServletRequest httpServletRequest,
                                         final HttpServletResponse httpServletResponse,
                                         final Authentication authentication) {
        log.info("Authentication success for {}", authentication.getName());
    }

    private void onAuthenticationFailure(final HttpServletRequest request,
                                         final HttpServletResponse response,
                                         final AuthenticationException ex) throws IOException {
        log.info("Authentication failure", ex);
        final var code401 = HttpStatus.UNAUTHORIZED.value();
        final var errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.UNAUTHORIZED.getCode())
                .httpCode(code401)
                .message(ex.getMessage())
                .build();

        response.setStatus(code401);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
    }

    private static class RequestMatcherProxy implements RequestMatcher {
        private final Collection<RequestMatcher> requestMatchers;

        RequestMatcherProxy(final Collection<String> antPaths) {
            requestMatchers = antPaths.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());
        }

        @Override
        public boolean matches(final HttpServletRequest httpServletRequest) {
            return requestMatchers.stream().noneMatch(requestMatcher -> requestMatcher.matches(httpServletRequest));
        }
    }
}
