package com.photocard.controller;

import com.photocard.dto.PhotocardResponse;
import com.photocard.service.PhotocardService;
import com.photocard.service.AzureStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Photocard", description = "포토카드 관리 API")
public class PhotocardController {
    
    private final PhotocardService photocardService;
    private final AzureStorageService azureStorageService;
    
    /**
     * 포토카드 생성 (파일 업로드)
     * POST /api/photocards
     */
    @Operation(summary = "포토카드 생성", description = "이미지 파일을 업로드하여 포토카드를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "포토카드 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    // @PostMapping(value = "/photocards", consumes = "multipart/form-data")
    @PostMapping(value = "/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test endpoint working!");
    }

    @PostMapping(value = "/photocards")
    public ResponseEntity<PhotocardResponse> createPhotocard(
            @Parameter(description = "업로드할 이미지 파일", required = true) 
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "대화 ID", required = true)
            @RequestParam("conversationId") Long conversationId,
            @Parameter(description = "작품 ID", required = true)
            @RequestParam("artworkId") Long artworkId) {
        log.info("포토카드 생성 요청 - fileName: {}, size: {}, conversationId: {}, artworkId: {}", 
                file.getOriginalFilename(), file.getSize(), conversationId, artworkId);
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            PhotocardResponse response = photocardService.createPhotocardWithFile(file, conversationId, artworkId);
            log.info("포토카드 생성 완료 - ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("포토카드 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 포토카드 조회
     * GET /api/photocards/{id}
     */
    @Operation(summary = "포토카드 조회", description = "ID로 포토카드를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "포토카드를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/photocards/{id}")
    public ResponseEntity<PhotocardResponse> getPhotocard(
            @Parameter(description = "포토카드 ID", required = true) @PathVariable Long id) {
        log.info("포토카드 조회 요청: {}", id);
        
        try {
            PhotocardResponse response = photocardService.getPhotocardById(id);
            log.info("포토카드 조회 성공: {}", response);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("포토카드를 찾을 수 없음 - ID: {}, 오류: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("포토카드 조회 중 오류 발생 - ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 세션별 포토카드 목록 조회
     * GET /api/photocards?sessionId={sessionId}
     */
    @Operation(summary = "세션별 포토카드 목록 조회", description = "세션 ID로 포토카드 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/photocards")
    public ResponseEntity<List<PhotocardResponse>> getPhotocardsBySession(
            @Parameter(description = "대화 ID", required = true) @RequestParam(name = "conversationId") Long conversationId) {
        log.info("대화별 포토카드 조회 요청: {}", conversationId);
        
        try {
            List<PhotocardResponse> responses = photocardService.getPhotocardsBySessionId(conversationId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("대화별 포토카드 조회 실패: {}", conversationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 작품 선택 (Chat-Orchestra에서 호출)
     * POST /api/conversation/{sessionId}/artworks/{artworkId}/select
     */
    @Operation(summary = "작품 선택", description = "사용자가 선택한 작품으로 포토카드를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작품 선택 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/conversation/{sessionId}/artworks/{artworkId}/select", consumes = "application/json")
    public ResponseEntity<PhotocardResponse> selectArtwork(
            @Parameter(description = "대화 ID", required = true) @PathVariable("sessionId") Long conversationId,
            @Parameter(description = "작품 ID", required = true) @PathVariable Long artworkId,
            @RequestBody(required = false) String body) {
        log.info("작품 선택 요청 - conversationId: {}, artworkId: {}", conversationId, artworkId);
        
        try {
            PhotocardResponse response = photocardService.selectArtwork(conversationId, artworkId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("작품 선택 실패 - conversationId: {}, artworkId: {}", conversationId, artworkId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 포토카드 다운로드
     * GET /api/photocards/{fileId}/download
     */
    @Operation(summary = "포토카드 다운로드", description = "포토카드 이미지를 다운로드합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/photocards/{fileId}/download")
    public ResponseEntity<Resource> downloadPhotocard(
            @Parameter(description = "파일 ID", required = true) @PathVariable String fileId) {
        log.info("포토카드 다운로드 요청: {}", fileId);
        
        try {
            Resource resource = azureStorageService.loadPhotocardImage(fileId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"photocard_" + fileId + ".jpg\"")
                    .body(resource);
        } catch (RuntimeException e) {
            log.error("포토카드 다운로드 실패 - fileId: {}, 오류: {}", fileId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("포토카드 다운로드 중 오류 발생 - fileId: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 포토카드 미리보기
     * GET /api/photocards/{fileId}/preview
     */
    @Operation(summary = "포토카드 미리보기", description = "포토카드 이미지를 미리보기합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "미리보기 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/photocards/{fileId}/preview")
    public ResponseEntity<Resource> previewPhotocard(
            @Parameter(description = "파일 ID", required = true) @PathVariable String fileId) {
        log.info("포토카드 미리보기 요청: {}", fileId);
        
        try {
            Resource resource = azureStorageService.loadPhotocardImage(fileId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (RuntimeException e) {
            log.error("포토카드 미리보기 실패 - fileId: {}, 오류: {}", fileId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("포토카드 미리보기 중 오류 발생 - fileId: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
