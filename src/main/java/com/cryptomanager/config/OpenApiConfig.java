package com.cryptomanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Classe responsavel pela configuracao do OpenAPI */
@Configuration
public class OpenApiConfig {

    /** Metodo responsavel pela configuracao personalizada da API.
     * @return Retorna uma instancia do OpenAPI configurada com as informacoees da API.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crypto Manager API")
                        .version("1.0")
                        .description("API para gerenciamento de criptomoedas"));
    }
}