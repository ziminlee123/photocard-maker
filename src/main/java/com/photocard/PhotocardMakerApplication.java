package com.photocard;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhotocardMakerApplication {

    public static void main(String[] args) {
        // .env 파일 로드 (로컬 개발용)
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            // 환경변수 설정
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
        } catch (Exception e) {
            // .env 파일이 없거나 로드 실패 시 무시 (Azure에서는 환경변수 사용)
            System.out.println("Warning: .env file not found or failed to load. Using system environment variables.");
        }
        
        SpringApplication.run(PhotocardMakerApplication.class, args);
    }

}
