package com.photocard.service;

import com.photocard.dto.ExternalArtworkResponse;
import com.photocard.dto.PhotocardCreateRequest;
import com.photocard.dto.PhotocardResponse;
import com.photocard.entity.Photocard;
import com.photocard.repository.PhotocardRepository;
import com.photocard.service.MetadataCombinationService.PhotocardMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PhotocardService {
    
    private final PhotocardRepository photocardRepository;
    private final ExternalApiService externalApiService;
    private final MetadataCombinationService metadataCombinationService;
    private final PhotocardFileService photocardFileService;
    private final ImageProcessingService imageProcessingService;
    
    /**
     * 포토카드 생성
     */
    public PhotocardResponse createPhotocard(PhotocardCreateRequest request) {
        log.info("포토카드 생성 시작 - artworkId: {}, sessionId: {}", request.getArtworkId(), request.getSessionId());
        
        // Exhibition 서비스에서 작품 정보 조회
        ExternalArtworkResponse artwork = externalApiService.getArtworkById(request.getArtworkId());
        
        // Chat-Orchestra 서비스에서 엔딩크레딧 조회
        var endingCredit = externalApiService.getEndingCreditBySessionId(request.getSessionId());
        
        // 메타데이터 조합
        PhotocardMetadata metadata = metadataCombinationService.combineMetadata(
                artwork, endingCredit, request.getSessionId());
        
        // 이미지 라이선스 확인
        boolean hasLicense = externalApiService.checkImageLicense(artwork.getImageUrl());
        if (!hasLicense) {
            throw new RuntimeException("이미지 라이선스가 없어 포토카드를 생성할 수 없습니다.");
        }
        
        // 포토카드 생성 (통합된 렌더링)
        PhotocardResponse response = createPhotocardInternal(request, artwork, endingCredit, metadata);
        
        return response;
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
     * 포토카드 생성 내부 로직 (통합된 렌더링)
     */
    private PhotocardResponse createPhotocardInternal(PhotocardCreateRequest request, 
                                                     ExternalArtworkResponse artwork, 
                                                     com.photocard.dto.EndingCreditResponse endingCredit, 
                                                     PhotocardMetadata metadata) {
        try {
            log.info("포토카드 렌더링 시작 - artworkId: {}", request.getArtworkId());
            
            // 1. 포토카드 이미지 생성 (기본 템플릿 사용)
            byte[] photocardImage = imageProcessingService.generatePhotocardImage(artwork, endingCredit);
            
            // 2. 파일 저장
            String fileId = photocardFileService.savePhotocardImage(photocardImage);
            
            // 3. 포토카드 엔티티 생성
            Photocard photocard = Photocard.builder()
                    .artworkId(request.getArtworkId())
                    .sessionId(request.getSessionId())
                    .title(request.getTitle() != null ? request.getTitle() : artwork.getTitle())
                    .description(request.getDescription() != null ? request.getDescription() : artwork.getDescription())
                    .endingCreditId(metadata.getEndingCreditId())
                    .conversationSummary(metadata.getConversationSummary())
                    .artworkMetadata(metadata.getArtworkMetadata())
                    .endingCreditMetadata(metadata.getEndingCreditMetadata())
                    .combinedMetadata(metadata.getCombinedMetadata())
                    .previewUrl(photocardFileService.generatePreviewUrl(fileId))
                    .downloadUrl(photocardFileService.generateDownloadUrl(fileId))
                    .status(Photocard.PhotocardStatus.COMPLETED)
                    .build();
            
            Photocard savedPhotocard = photocardRepository.save(photocard);
            log.info("포토카드 생성 완료 - id: {}, fileId: {}, size: {} bytes", 
                    savedPhotocard.getId(), fileId, photocardImage.length);
            
            return PhotocardResponse.from(savedPhotocard);
                    
        } catch (Exception e) {
            log.error("포토카드 생성 실패 - artworkId: {}", request.getArtworkId(), e);
            throw new RuntimeException("포토카드 생성에 실패했습니다: " + e.getMessage());
        }
    }
}