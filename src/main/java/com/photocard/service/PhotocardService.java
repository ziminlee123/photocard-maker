package com.photocard.service;

import com.photocard.dto.ExternalArtworkResponse;
import com.photocard.dto.PhotocardCreateRequest;
import com.photocard.dto.PhotocardResponse;
import com.photocard.entity.ArtworkSelection;
import com.photocard.entity.Photocard;
import com.photocard.repository.ArtworkSelectionRepository;
import com.photocard.repository.PhotocardRepository;
import com.photocard.service.MetadataCombinationService.PhotocardMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PhotocardService {
    
    private final PhotocardRepository photocardRepository;
    private final ArtworkSelectionRepository artworkSelectionRepository;
    private final ExternalApiService externalApiService;
    private final MetadataCombinationService metadataCombinationService;
    private final AzureStorageService azureStorageService;
    private final ImageProcessingService imageProcessingService;
    
    /**
     * 포토카드 생성
     */
    public PhotocardResponse createPhotocard(PhotocardCreateRequest request) {
        log.info("포토카드 생성 시작 - artworkId: {}, conversationId: {}", request.getArtworkId(), request.getConversationId());
        
        // 1. Exhibition API에서 artwork 정보 가져오기 (image_url 포함)
        ExternalArtworkResponse artwork = externalApiService.getArtworkById(request.getArtworkId());
        if (artwork == null) {
            throw new RuntimeException("작품을 찾을 수 없습니다: " + request.getArtworkId());
        }
        
        // 2. Chat-Orchestra API에서 엔딩크레딧 가져오기
        com.photocard.dto.EndingCreditResponse endingCredit = externalApiService.getEndingCreditBySessionId(request.getConversationId());
        if (endingCredit == null) {
            throw new RuntimeException("엔딩 크레딧을 찾을 수 없습니다: " + request.getConversationId());
        }
        
        // 3. conversations 테이블에 데이터가 없으면 자동 생성
        ensureConversationExists(request.getConversationId());
        
        // 4. 작품 사진 + 엔딩크레딧 조합해서 포토카드 생성
        PhotocardResponse response = createPhotocardWithEndingCredit(request, artwork, endingCredit);
        
        return response;
    }
    
    /**
     * 작품 사진 + 엔딩크레딧 조합 포토카드 생성
     */
    private PhotocardResponse createPhotocardWithEndingCredit(PhotocardCreateRequest request, 
                                                             ExternalArtworkResponse artwork, 
                                                             com.photocard.dto.EndingCreditResponse endingCredit) {
        try {
            log.info("작품 사진 + 엔딩크레딧 조합 포토카드 생성 시작 - artworkId: {}", request.getArtworkId());
            
            // 1. 작품 사진 + 엔딩크레딧으로 포토카드 이미지 생성
            byte[] photocardImage = imageProcessingService.generatePhotocardImage(artwork, endingCredit);
            
            // 2. Azure Storage에 파일 저장
            String fileId = azureStorageService.savePhotocardImage(photocardImage);
            
            // 3. 포토카드 엔티티 생성
            Photocard photocard = Photocard.builder()
                    .artworkId(request.getArtworkId())
                    .conversationId(request.getConversationId())
                    .downloadUrl(azureStorageService.generateDownloadUrl(fileId))
                    .build();
            
            Photocard savedPhotocard = photocardRepository.save(photocard);
            log.info("작품 사진 + 엔딩크레딧 조합 포토카드 생성 완료 - id: {}, fileId: {}, size: {} bytes", 
                    savedPhotocard.getId(), fileId, photocardImage.length);
            
            return PhotocardResponse.from(savedPhotocard);
                    
        } catch (Exception e) {
            log.error("작품 사진 + 엔딩크레딧 조합 포토카드 생성 실패 - artworkId: {}", request.getArtworkId(), e);
            throw new RuntimeException("포토카드 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 간단한 포토카드 생성 (메타데이터 없이)
     */
    private PhotocardResponse createSimplePhotocard(PhotocardCreateRequest request, ExternalArtworkResponse artwork) {
        try {
            log.info("간단한 포토카드 생성 시작 - artworkId: {}", request.getArtworkId());
            
            // 1. 기본 템플릿으로 포토카드 이미지 생성
            byte[] photocardImage = imageProcessingService.generateSimplePhotocardImage(artwork);
            
            // 2. Azure Storage에 파일 저장
            String fileId = azureStorageService.savePhotocardImage(photocardImage);
            
            // 3. 포토카드 엔티티 생성
            Photocard photocard = Photocard.builder()
                    .artworkId(request.getArtworkId())
                    .conversationId(request.getConversationId())
                    .downloadUrl(azureStorageService.generateDownloadUrl(fileId))
                    .build();
            
            Photocard savedPhotocard = photocardRepository.save(photocard);
            log.info("간단한 포토카드 생성 완료 - id: {}, fileId: {}, size: {} bytes", 
                    savedPhotocard.getId(), fileId, photocardImage.length);
            
            return PhotocardResponse.from(savedPhotocard);
                    
        } catch (Exception e) {
            log.error("간단한 포토카드 생성 실패 - artworkId: {}", request.getArtworkId(), e);
            throw new RuntimeException("포토카드 생성에 실패했습니다: " + e.getMessage());
        }
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
    public List<PhotocardResponse> getPhotocardsBySessionId(Long conversationId) {
        List<Photocard> photocards = photocardRepository.findByConversationId(conversationId);
        return photocards.stream()
                .map(PhotocardResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 작품 선택 처리 (Chat-Orchestra에서 호출)
     */
    public PhotocardResponse selectArtwork(Long conversationId, Long artworkId) {
        log.info("작품 선택 처리 - conversationId: {}, artworkId: {}", conversationId, artworkId);
        
        // 1. artwork_selections 테이블에 선택 기록 저장
        saveArtworkSelection(conversationId, artworkId);
        
        // 2. 이미 해당 세션에서 같은 작품으로 포토카드가 생성되었는지 확인
        return photocardRepository.findByConversationIdAndArtworkId(conversationId, artworkId)
                .map(PhotocardResponse::from)
                .orElseGet(() -> {
                    // 3. 새로운 포토카드 생성
                    PhotocardCreateRequest request = PhotocardCreateRequest.builder()
                            .artworkId(artworkId)
                            .conversationId(conversationId)
                            .build();
                    return createPhotocard(request);
                });
    }
    
    /**
     * 테스트용 포토카드 생성 (External API 우회)
     */
    public PhotocardResponse createTestPhotocard() {
        log.info("테스트 포토카드 생성 시작");
        
        try {
            // 1. 더미 이미지 생성 (External API 없이)
            byte[] testImage = generateDummyImage();
            
            // 2. Azure Storage에 파일 저장
            String fileId = azureStorageService.savePhotocardImage(testImage);
            
            // 3. 포토카드 엔티티 생성
            Photocard photocard = Photocard.builder()
                    .artworkId(1L)
                    .conversationId(1L)
                    .downloadUrl(azureStorageService.generateDownloadUrl(fileId))
                    .build();
            
            Photocard savedPhotocard = photocardRepository.save(photocard);
            log.info("테스트 포토카드 생성 완료 - id: {}, fileId: {}", savedPhotocard.getId(), fileId);
            
            return PhotocardResponse.from(savedPhotocard);
                    
        } catch (Exception e) {
            log.error("테스트 포토카드 생성 실패", e);
            throw new RuntimeException("테스트 포토카드 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 더미 이미지 생성
     */
    private byte[] generateDummyImage() {
        try {
            BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // 배경색
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 600, 400);
            
            // 텍스트
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Test Photocard", 200, 200);
            
            g2d.dispose();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("더미 이미지 생성 실패", e);
        }
    }
    
    /**
     * conversations 테이블에 데이터가 없으면 자동 생성
     */
    private void ensureConversationExists(Long conversationId) {
        // TODO: conversations 테이블에 해당 ID가 있는지 확인하고 없으면 생성
        // 현재는 외래키 제약조건 오류를 방지하기 위한 임시 처리
        log.info("conversationId {} 확인 - conversations 테이블에 데이터가 있는지 확인 필요", conversationId);
    }
    
    /**
     * 작품 선택 기록 저장
     */
    private void saveArtworkSelection(Long conversationId, Long artworkId) {
        // 중복 선택 방지
        if (!artworkSelectionRepository.existsByConversationIdAndArtworkId(conversationId, artworkId)) {
            ArtworkSelection selection = ArtworkSelection.builder()
                    .conversationId(conversationId)
                    .artworkId(artworkId)
                    .build();
            artworkSelectionRepository.save(selection);
            log.info("작품 선택 기록 저장 완료 - conversationId: {}, artworkId: {}", conversationId, artworkId);
        } else {
            log.info("이미 선택된 작품 - conversationId: {}, artworkId: {}", conversationId, artworkId);
        }
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
            
            // 2. Azure Storage에 파일 저장
            String fileId = azureStorageService.savePhotocardImage(photocardImage);
            
            // 3. 포토카드 엔티티 생성
            Photocard photocard = Photocard.builder()
                    .artworkId(request.getArtworkId())
                    .conversationId(request.getConversationId())
                    .downloadUrl(azureStorageService.generateDownloadUrl(fileId))
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