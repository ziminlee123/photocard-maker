package com.photocard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Photocard-Maker API")
                        .description("MSA 팀 프로젝트의 포토카드 생성 서비스 API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("김대희")
                                .email("kimdaehee@example.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("개발 서버")
                ));
    }
}
