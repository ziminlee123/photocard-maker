package com.photocard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

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
                                .description("로컬 개발 서버"),
                        new Server()
                                .url("https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net")
                                .description("Azure 프로덕션 서버")
                ));
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false) // credentials를 false로 변경하여 CORS 오류 방지
                .maxAge(3600);
        
        // 다운로드 엔드포인트에 대한 별도 CORS 설정
        registry.addMapping("/api/photocards/*/download")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
                
        // 미리보기 엔드포인트에 대한 별도 CORS 설정
        registry.addMapping("/api/photocards/*/preview")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false); // credentials를 false로 변경
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        // 다운로드 엔드포인트에 대한 별도 설정
        CorsConfiguration downloadConfig = new CorsConfiguration();
        downloadConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        downloadConfig.setAllowedMethods(Arrays.asList("GET", "OPTIONS"));
        downloadConfig.setAllowedHeaders(Arrays.asList("*"));
        downloadConfig.setAllowCredentials(false);
        downloadConfig.setMaxAge(3600L);
        source.registerCorsConfiguration("/api/photocards/*/download", downloadConfig);
        
        // 미리보기 엔드포인트에 대한 별도 설정
        CorsConfiguration previewConfig = new CorsConfiguration();
        previewConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        previewConfig.setAllowedMethods(Arrays.asList("GET", "OPTIONS"));
        previewConfig.setAllowedHeaders(Arrays.asList("*"));
        previewConfig.setAllowCredentials(false);
        previewConfig.setMaxAge(3600L);
        source.registerCorsConfiguration("/api/photocards/*/preview", previewConfig);
        
        return source;
    }
}
