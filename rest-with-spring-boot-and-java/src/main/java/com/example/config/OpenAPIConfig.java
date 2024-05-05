package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful API with Java 18 and Spring Boot 3 - Foo Bar")
                        .version("v1")
                        .description("Some description about your API")
                        .termsOfService("https://github.com/anderson-rodrigues-dev")
                        .license(
                                new License()
                                        .name("Apache 2.0")
                                        .url("https://github.com/anderson-rodrigues-dev")));
    }
}
