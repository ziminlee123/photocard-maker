package com.photocard.controller;

import com.photocard.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File", description = "파일 관리 API")
public class FileController {
    
    private final FileStorageService fileStorageService;
    
    /**
     * 미리보기 파일 조회
     * GET /api/files/preview/{fileId}
     */
    @Operation(summary = "미리보기 파일 조회", description = "파일 ID로 미리보기 파일을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<Resource> getPreview(
            @Parameter(description = "파일 ID", required = true)
            @PathVariable String fileId) {
        
        log.info("미리보기 파일 조회 요청 - fileId: {}", fileId);
        
        try {
            if (!fileStorageService.fileExists(fileId)) {
                log.warn("파일을 찾을 수 없음 - fileId: {}", fileId);
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileData = fileStorageService.getFile(fileId);
            ByteArrayResource resource = new ByteArrayResource(fileData);
            
            // MIME 타입 설정
            MediaType mediaType = getMediaType(fileId);
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(fileData.length)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("미리보기 파일 조회 실패 - fileId: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 다운로드 파일 조회
     * GET /api/files/download/{fileId}
     */
    @Operation(summary = "다운로드 파일 조회", description = "파일 ID로 다운로드 파일을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(
            @Parameter(description = "파일 ID", required = true)
            @PathVariable String fileId) {
        
        log.info("다운로드 파일 조회 요청 - fileId: {}", fileId);
        
        try {
            if (!fileStorageService.fileExists(fileId)) {
                log.warn("파일을 찾을 수 없음 - fileId: {}", fileId);
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileData = fileStorageService.getFile(fileId);
            ByteArrayResource resource = new ByteArrayResource(fileData);
            
            // MIME 타입 설정
            MediaType mediaType = getMediaType(fileId);
            
            // 다운로드용 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .contentLength(fileData.length)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("다운로드 파일 조회 실패 - fileId: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 파일 업로드
     * POST /api/files/upload
     */
    @Operation(summary = "파일 업로드", description = "파일을 업로드하고 파일 ID를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(description = "업로드할 파일", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("파일 업로드 요청 - fileName: {}, size: {}", file.getOriginalFilename(), file.getSize());
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String fileId = fileStorageService.saveFile(file);
            String previewUrl = fileStorageService.generatePreviewUrl(fileId);
            String downloadUrl = fileStorageService.generateDownloadUrl(fileId);
            
            FileUploadResponse response = FileUploadResponse.builder()
                    .fileId(fileId)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .previewUrl(previewUrl)
                    .downloadUrl(downloadUrl)
                    .build();
            
            log.info("파일 업로드 완료 - fileId: {}", fileId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("파일 업로드 실패 - fileName: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 파일 삭제
     * DELETE /api/files/{fileId}
     */
    @Operation(summary = "파일 삭제", description = "파일 ID로 파일을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "파일 ID", required = true)
            @PathVariable String fileId) {
        
        log.info("파일 삭제 요청 - fileId: {}", fileId);
        
        try {
            boolean deleted = fileStorageService.deleteFile(fileId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("파일 삭제 실패 - fileId: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 파일 확장자에 따른 MIME 타입 반환
     */
    private MediaType getMediaType(String fileId) {
        if (fileId.endsWith(".jpg") || fileId.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (fileId.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (fileId.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (fileId.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
    
    /**
     * 파일 업로드 응답 DTO
     */
    public static class FileUploadResponse {
        private String fileId;
        private String fileName;
        private long fileSize;
        private String contentType;
        private String previewUrl;
        private String downloadUrl;
        
        public static FileUploadResponseBuilder builder() {
            return new FileUploadResponseBuilder();
        }
        
        // Getters and Setters
        public String getFileId() { return fileId; }
        public void setFileId(String fileId) { this.fileId = fileId; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public String getPreviewUrl() { return previewUrl; }
        public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
        
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        
        public static class FileUploadResponseBuilder {
            private String fileId;
            private String fileName;
            private long fileSize;
            private String contentType;
            private String previewUrl;
            private String downloadUrl;
            
            public FileUploadResponseBuilder fileId(String fileId) {
                this.fileId = fileId;
                return this;
            }
            
            public FileUploadResponseBuilder fileName(String fileName) {
                this.fileName = fileName;
                return this;
            }
            
            public FileUploadResponseBuilder fileSize(long fileSize) {
                this.fileSize = fileSize;
                return this;
            }
            
            public FileUploadResponseBuilder contentType(String contentType) {
                this.contentType = contentType;
                return this;
            }
            
            public FileUploadResponseBuilder previewUrl(String previewUrl) {
                this.previewUrl = previewUrl;
                return this;
            }
            
            public FileUploadResponseBuilder downloadUrl(String downloadUrl) {
                this.downloadUrl = downloadUrl;
                return this;
            }
            
            public FileUploadResponse build() {
                FileUploadResponse response = new FileUploadResponse();
                response.setFileId(this.fileId);
                response.setFileName(this.fileName);
                response.setFileSize(this.fileSize);
                response.setContentType(this.contentType);
                response.setPreviewUrl(this.previewUrl);
                response.setDownloadUrl(this.downloadUrl);
                return response;
            }
        }
    }
}
