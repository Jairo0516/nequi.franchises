package com.nequi.franchises.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Nequi Franchises API",
                version = "v1",
                description = "API para gestionar franquicias, sucursales y productos (stock)."
        )
)
public class OpenApiConfig { }
