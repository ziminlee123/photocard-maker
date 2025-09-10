package com.photocard.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.UUID;

@Service
@Slf4j
public class AzureStorageService {
    
    @Value("${azure.storage.connection-string}")
    private String connectionString;
    
    @Value("${azure.storage.container-name:photocards}")
    private String containerName;
    
    @Value("${azure.storage.base-url:https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net}")
    private String baseUrl;
    
    private BlobServiceClient getBlobServiceClient() {
        if (connectionString == null || connectionString.trim().isEmpty()) {
            log.error("Azure Storage 연결 문자열이 설정되지 않았습니다. connectionString: '{}'", connectionString);
            throw new RuntimeException("Azure Storage 연결 문자열이 설정되지 않았습니다. AZURE_STORAGE_CONNECTION_STRING 환경변수를 확인하세요.");
        }
        
        try {
            return new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
        } catch (Exception e) {
            log.error("Azure Storage 연결 실패: {}", e.getMessage());
            throw new RuntimeException("Azure Storage 연결에 실패했습니다: " + e.getMessage());
        }
    }
    
    private BlobContainerClient getContainerClient() {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        
        // 컨테이너가 없으면 생성
        if (!containerClient.exists()) {
            containerClient.create();
            log.info("Azure Storage 컨테이너 생성: {}", containerName);
        }
        
        return containerClient;
    }
    
    /**
     * 포토카드 이미지를 Azure Storage에 저장
     */
    public String savePhotocardImage(byte[] imageData) {
        try {
            String fileId = UUID.randomUUID().toString();
            String fileName = "photocard_" + fileId + ".jpg";
            
            BlobContainerClient containerClient = getContainerClient();
            BlobClient blobClient = containerClient.getBlobClient(fileName);
            
            // 이미지 데이터 업로드
            blobClient.upload(new ByteArrayInputStream(imageData), imageData.length, true);
            
            log.info("Azure Storage에 포토카드 이미지 저장 완료 - fileId: {}, size: {} bytes", fileId, imageData.length);
            return fileId;
            
        } catch (Exception e) {
            log.error("Azure Storage 이미지 저장 실패", e);
            throw new RuntimeException("파일 저장에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Azure Storage에서 포토카드 이미지 로드
     */
    public Resource loadPhotocardImage(String fileId) {
        try {
            String fileName = "photocard_" + fileId + ".jpg";
            BlobContainerClient containerClient = getContainerClient();
            BlobClient blobClient = containerClient.getBlobClient(fileName);
            
            if (blobClient.exists()) {
                URL blobUrl = new URL(blobClient.getBlobUrl());
                return new UrlResource(blobUrl);
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
            }
        } catch (Exception e) {
            log.error("Azure Storage 이미지 로드 실패 - fileId: {}", fileId, e);
            throw new RuntimeException("파일 로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Azure Storage에서 포토카드 이미지 삭제
     */
    public void deletePhotocardImage(String fileId) {
        try {
            String fileName = "photocard_" + fileId + ".jpg";
            BlobContainerClient containerClient = getContainerClient();
            BlobClient blobClient = containerClient.getBlobClient(fileName);
            
            if (blobClient.exists()) {
                blobClient.delete();
                log.info("Azure Storage 이미지 삭제 완료 - fileId: {}", fileId);
            }
        } catch (Exception e) {
            log.error("Azure Storage 이미지 삭제 실패 - fileId: {}", fileId, e);
        }
    }
    
    /**
     * 다운로드 URL 생성
     */
    public String generateDownloadUrl(String fileId) {
        return baseUrl + "/api/photocards/" + fileId + "/download";
    }
    
    /**
     * 미리보기 URL 생성
     */
    public String generatePreviewUrl(String fileId) {
        return baseUrl + "/api/photocards/" + fileId + "/preview";
    }
}
