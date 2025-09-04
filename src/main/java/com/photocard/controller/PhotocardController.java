package com.photocard.controller;

import com.photocard.dto.PhotocardCreateRequest;
import com.photocard.dto.PhotocardResponse;
import com.photocard.service.PhotocardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Photocard", description = "포토카드 관리 API")
public class PhotocardController {
    
    private final PhotocardService photocardService;
    
    /**
     * 포토카드 생성
     * POST /api/photocards
     */
    @Operation(summary = "포토카드 생성", description = "작품 정보를 기반으로 포토카드를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "포토카드 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/photocards")
    public ResponseEntity<PhotocardResponse> createPhotocard(@Valid @RequestBody PhotocardCreateRequest request) {
        log.info("포토카드 생성 요청: {}", request);
        
        try {
            PhotocardResponse response = photocardService.createPhotocard(request);
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
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("포토카드 조회 실패: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("포토카드 조회 실패: {}", id, e);
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
            @Parameter(description = "세션 ID", required = true) @RequestParam String sessionId) {
        log.info("세션별 포토카드 조회 요청: {}", sessionId);
        
        try {
            List<PhotocardResponse> responses = photocardService.getPhotocardsBySessionId(sessionId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("세션별 포토카드 조회 실패: {}", sessionId, e);
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
    @PostMapping("/conversation/{sessionId}/artworks/{artworkId}/select")
    public ResponseEntity<PhotocardResponse> selectArtwork(
            @Parameter(description = "세션 ID", required = true) @PathVariable String sessionId,
            @Parameter(description = "작품 ID", required = true) @PathVariable Long artworkId) {
        log.info("작품 선택 요청 - sessionId: {}, artworkId: {}", sessionId, artworkId);
        
        try {
            PhotocardResponse response = photocardService.selectArtwork(sessionId, artworkId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("작품 선택 실패 - sessionId: {}, artworkId: {}", sessionId, artworkId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
