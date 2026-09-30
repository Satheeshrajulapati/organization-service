package com.employee.organization.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI organizationServiceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Organization Service API")
                        .description(
                                "APIs for managing organizational master data " +
                                        "such as departments, designations, locations " +
                                        "and employment types."
                        )
                        .version("v1")
                        .contact(new Contact()
                                .name("Employee Management Platform")));
    }
}