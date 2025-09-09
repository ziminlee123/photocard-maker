package com.photocard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    @Value("${file.base-url:http://localhost:8081}")
    private String baseUrl;
    
    /**
     * 파일을 저장하고 파일 ID 반환
     */
    public String saveFile(byte[] fileData, String fileName, String fileType) {
        try {
            // 파일 ID 생성
            String fileId = UUID.randomUUID().toString();
            String extension = getFileExtension(fileName);
            String savedFileName = fileId + extension;
            
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 파일 저장
            Path filePath = uploadPath.resolve(savedFileName);
            Files.write(filePath, fileData);
            
            log.info("파일 저장 완료 - fileId: {}, path: {}", fileId, filePath);
            return fileId;
            
        } catch (IOException e) {
            log.error("파일 저장 실패 - fileName: {}", fileName, e);
            throw new RuntimeException("파일 저장에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * MultipartFile을 저장하고 파일 ID 반환
     */
    public String saveFile(MultipartFile file) {
        try {
            return saveFile(file.getBytes(), file.getOriginalFilename(), file.getContentType());
        } catch (IOException e) {
            log.error("MultipartFile 저장 실패 - fileName: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 저장에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 파일 ID로 파일 조회
     */
    public byte[] getFile(String fileId) {
        try {
            Path filePath = getFilePath(fileId);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fileId);
            }
            
            return Files.readAllBytes(filePath);
            
        } catch (IOException e) {
            log.error("파일 조회 실패 - fileId: {}", fileId, e);
            throw new RuntimeException("파일 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 파일 ID로 파일 경로 조회
     */
    public Path getFilePath(String fileId) {
        Path uploadPath = Paths.get(uploadDir);
        return uploadPath.resolve(fileId);
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String fileId) {
        Path filePath = getFilePath(fileId);
        return Files.exists(filePath);
    }
    
    /**
     * 파일 삭제
     */
    public boolean deleteFile(String fileId) {
        try {
            Path filePath = getFilePath(fileId);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 완료 - fileId: {}", fileId);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("파일 삭제 실패 - fileId: {}", fileId, e);
            return false;
        }
    }
    
    /**
     * 미리보기 URL 생성
     */
    public String generatePreviewUrl(String fileId) {
        return baseUrl + "/api/files/preview/" + fileId;
    }
    
    /**
     * 다운로드 URL 생성
     */
    public String generateDownloadUrl(String fileId) {
        return baseUrl + "/api/files/download/" + fileId;
    }
    
    /**
     * CDN URL 생성 (향후 CDN 연동 시 사용)
     */
    public String generateCdnUrl(String fileId) {
        // 실제 CDN URL 생성 로직
        // 현재는 로컬 URL 반환
        return generateDownloadUrl(fileId);
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
}
