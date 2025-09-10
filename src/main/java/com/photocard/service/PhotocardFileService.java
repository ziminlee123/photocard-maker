package com.photocard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotocardFileService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${file.base-url}")
    private String fileBaseUrl;
    
    /**
     * 포토카드 이미지 저장
     */
    public String savePhotocardImage(byte[] imageData) {
        try {
            // 파일 ID 생성
            String fileId = UUID.randomUUID().toString();
            String fileName = "photocard_" + fileId + ".jpg";
            
            // 저장 경로 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, imageData);
            
            log.info("포토카드 이미지 저장 완료 - fileId: {}, size: {} bytes", fileId, imageData.length);
            return fileId;
            
        } catch (IOException e) {
            log.error("포토카드 이미지 저장 실패", e);
            throw new RuntimeException("파일 저장에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 포토카드 이미지 로드
     */
    public Resource loadPhotocardImage(String fileId) {
        try {
            String fileName = "photocard_" + fileId + ".jpg";
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
            }
        } catch (Exception e) {
            log.error("포토카드 이미지 로드 실패 - fileId: {}", fileId, e);
            throw new RuntimeException("파일 로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 포토카드 이미지 삭제
     */
    public void deletePhotocardImage(String fileId) {
        try {
            String fileName = "photocard_" + fileId + ".jpg";
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
            log.info("포토카드 이미지 삭제 완료 - fileId: {}", fileId);
        } catch (IOException e) {
            log.error("포토카드 이미지 삭제 실패 - fileId: {}", fileId, e);
        }
    }
    
    /**
     * 다운로드 URL 생성
     */
    public String generateDownloadUrl(String fileId) {
        return fileBaseUrl + "/api/photocards/" + fileId + "/download";
    }
    
    /**
     * 미리보기 URL 생성
     */
    public String generatePreviewUrl(String fileId) {
        return fileBaseUrl + "/api/photocards/" + fileId + "/preview";
    }
}
