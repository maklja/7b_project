package com.sevenb.task.api.config;

import com.sevenb.task.api.controllers.version.ApiV1;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import static org.springframework.web.method.HandlerTypePredicate.forAnnotation;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    public static final String V1_ROUTES_PREFIX = "/api/v1";

    @Override
    protected void configurePathMatch(final PathMatchConfigurer configurer) {
        configurer.addPathPrefix(V1_ROUTES_PREFIX,
                forAnnotation(RestController.class).and(HandlerTypePredicate.forAnnotation(ApiV1.class))
        );
    }
}
