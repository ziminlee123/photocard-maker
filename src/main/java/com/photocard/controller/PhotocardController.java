package com.photocard.controller;

import com.photocard.dto.PhotocardCreateRequest;
import com.photocard.dto.PhotocardResponse;
import com.photocard.service.PhotocardService;
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
public class PhotocardController {
    
    private final PhotocardService photocardService;
    
    /**
     * 포토카드 생성
     * POST /api/photocards
     */
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
    @GetMapping("/photocards/{id}")
    public ResponseEntity<PhotocardResponse> getPhotocard(@PathVariable Long id) {
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
    @GetMapping("/photocards")
    public ResponseEntity<List<PhotocardResponse>> getPhotocardsBySession(
            @RequestParam String sessionId) {
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
    @PostMapping("/conversation/{sessionId}/artworks/{artworkId}/select")
    public ResponseEntity<PhotocardResponse> selectArtwork(
            @PathVariable String sessionId,
            @PathVariable Long artworkId) {
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
