package com.photocard.service;

import com.photocard.dto.ExternalArtworkResponse;
import com.photocard.dto.PhotocardCreateRequest;
import com.photocard.dto.PhotocardResponse;
import com.photocard.entity.Photocard;
import com.photocard.repository.PhotocardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PhotocardService {
    
    private final PhotocardRepository photocardRepository;
    private final ExternalApiService externalApiService;
    
    /**
     * 포토카드 생성
     */
    public PhotocardResponse createPhotocard(PhotocardCreateRequest request) {
        log.info("포토카드 생성 시작 - artworkId: {}, sessionId: {}", request.getArtworkId(), request.getSessionId());
        
        // Exhibition 서비스에서 작품 정보 조회
        ExternalArtworkResponse artwork = externalApiService.getArtworkById(request.getArtworkId());
        
        // 이미지 라이선스 확인
        boolean hasLicense = externalApiService.checkImageLicense(artwork.getImageUrl());
        if (!hasLicense) {
            throw new RuntimeException("이미지 라이선스가 없어 포토카드를 생성할 수 없습니다.");
        }
        
        // 포토카드 생성
        Photocard photocard = Photocard.builder()
                .artworkId(request.getArtworkId())
                .sessionId(request.getSessionId())
                .title(request.getTitle() != null ? request.getTitle() : artwork.getTitle())
                .description(request.getDescription() != null ? request.getDescription() : artwork.getDescription())
                .previewUrl(generatePreviewUrl())
                .downloadUrl(generateDownloadUrl())
                .status(Photocard.PhotocardStatus.GENERATING)
                .build();
        
        Photocard savedPhotocard = photocardRepository.save(photocard);
        log.info("포토카드 생성 완료 - id: {}", savedPhotocard.getId());
        
        // 비동기로 포토카드 렌더링 처리 (실제 구현에서는 별도 스레드나 큐 사용)
        processPhotocardRendering(savedPhotocard);
        
        return PhotocardResponse.from(savedPhotocard);
    }
    
    /**
     * 포토카드 조회
     */
    @Transactional(readOnly = true)
    public PhotocardResponse getPhotocardById(Long id) {
        Photocard photocard = photocardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("포토카드를 찾을 수 없습니다: " + id));
        
        return PhotocardResponse.from(photocard);
    }
    
    /**
     * 세션별 포토카드 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PhotocardResponse> getPhotocardsBySessionId(String sessionId) {
        List<Photocard> photocards = photocardRepository.findBySessionId(sessionId);
        return photocards.stream()
                .map(PhotocardResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 작품 선택 처리 (Chat-Orchestra에서 호출)
     */
    public PhotocardResponse selectArtwork(String sessionId, Long artworkId) {
        log.info("작품 선택 처리 - sessionId: {}, artworkId: {}", sessionId, artworkId);
        
        // 이미 해당 세션에서 같은 작품으로 포토카드가 생성되었는지 확인
        return photocardRepository.findBySessionIdAndArtworkId(sessionId, artworkId)
                .map(PhotocardResponse::from)
                .orElseGet(() -> {
                    // 새로운 포토카드 생성
                    PhotocardCreateRequest request = PhotocardCreateRequest.builder()
                            .artworkId(artworkId)
                            .sessionId(sessionId)
                            .build();
                    return createPhotocard(request);
                });
    }
    
    /**
     * 포토카드 렌더링 처리 (비동기)
     */
    private void processPhotocardRendering(Photocard photocard) {
        // 실제 구현에서는 이미지 생성 라이브러리나 AI 서비스를 사용
        // 여기서는 간단히 상태만 변경
        try {
            // 렌더링 시뮬레이션
            Thread.sleep(1000);
            
            photocard.setStatus(Photocard.PhotocardStatus.COMPLETED);
            photocardRepository.save(photocard);
            
            log.info("포토카드 렌더링 완료 - id: {}", photocard.getId());
        } catch (Exception e) {
            photocard.setStatus(Photocard.PhotocardStatus.FAILED);
            photocardRepository.save(photocard);
            log.error("포토카드 렌더링 실패 - id: {}", photocard.getId(), e);
        }
    }
    
    /**
     * 미리보기 URL 생성
     */
    private String generatePreviewUrl() {
        return "https://photocard-maker.com/preview/" + UUID.randomUUID().toString();
    }
    
    /**
     * 다운로드 URL 생성
     */
    private String generateDownloadUrl() {
        return "https://photocard-maker.com/download/" + UUID.randomUUID().toString();
    }
}