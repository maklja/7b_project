package com.sevenb.task.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenb.task.api.exceptions.ErrorCodes;
import com.sevenb.task.api.response.ErrorResponse;
import com.sevenb.task.api.security.XAuthenticationConverter;
import com.sevenb.task.api.security.provider.XAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final XAuthenticationConverter authConverter;
    private final XAuthenticationProvider authProvider;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .anonymous().disable()
                .csrf().disable()
                .formLogin().disable()
                .authenticationProvider(authProvider)
                .addFilterAfter(createAuthenticationFilter(), LogoutFilter.class);
    }

    private AuthenticationFilter createAuthenticationFilter() throws Exception {
        final var filter = new AuthenticationFilter(authenticationManager(), authConverter);
        filter.setSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
        });
        filter.setFailureHandler(new XAuthenticationFailureHandler());

        return filter;
    }

    private static class XAuthenticationFailureHandler implements AuthenticationFailureHandler {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void onAuthenticationFailure(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final AuthenticationException ex) throws IOException {

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
    }
}