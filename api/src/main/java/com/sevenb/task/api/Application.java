package com.sevenb.task.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.sevenb.task.*")
@OpenAPIDefinition(info = @Info(title = "7B project", version = "3.0", description = "7B project task api"))
public class Application {
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}