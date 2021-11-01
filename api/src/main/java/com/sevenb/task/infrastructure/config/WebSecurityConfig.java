package com.sevenb.task.infrastructure.config;

import com.sevenb.task.infrastructure.security.XAuthenticationConverter;
import com.sevenb.task.infrastructure.security.filter.XAuthenticationFilter;
import com.sevenb.task.infrastructure.security.provider.XAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Collection<String> unsecuredRoutes = Stream.of(
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    ).collect(Collectors.toUnmodifiableList());
    private final XAuthenticationConverter authConverter;
    private final XAuthenticationProvider authProvider;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers(unsecuredRoutes.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .formLogin().disable()
                .authenticationProvider(authProvider)
                .addFilterBefore(createAuthenticationFilter(), AnonymousAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationFilter createAuthenticationFilter() throws Exception {
        return new XAuthenticationFilter(unsecuredRoutes, authenticationManager(), authConverter);
    }
}