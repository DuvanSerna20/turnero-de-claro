package com.example.claro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI claroOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Claro Colombia — API REST")
                        .description("""
                                API del proyecto final: turnero inteligente con prioridades,
                                tienda de planes, compras y autenticación de usuarios.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sandra")
                                .email("sernaduvan4@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local")));
    }
}
