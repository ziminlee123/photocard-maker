package com.photocard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ExternalApiConfig {
    
    @Value("${external.exhibition.base-url}")
    private String exhibitionBaseUrl;
    
    @Value("${external.chat-orchestra.base-url}")
    private String chatOrchestraBaseUrl;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public String getExhibitionBaseUrl() {
        return exhibitionBaseUrl;
    }
    
    public String getChatOrchestraBaseUrl() {
        return chatOrchestraBaseUrl;
    }
}
