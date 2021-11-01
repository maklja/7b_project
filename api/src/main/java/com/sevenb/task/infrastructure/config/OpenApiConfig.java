package com.sevenb.task.infrastructure.config;

import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenApiConfig {

    @Bean
    public OperationCustomizer customize(final Environment env) {
        return (operation, handlerMethod) -> operation.addParametersItem(
                new Parameter()
                        .in("header")
                        .required(true)
                        .schema(new StringSchema())
                        .description("Security header that contains username")
                        .name(env.getRequiredProperty("security.authentication-header")));
    }
}
