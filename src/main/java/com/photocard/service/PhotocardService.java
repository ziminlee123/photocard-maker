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

import org.springframework.web.multipart.MultipartFile;

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
        log.info("포토카드 생성 시작 - artworkId: {}", request.getArtworkId());
        
        try {
            // 1. Exhibition API에서 artwork 정보 가져오기 (image_url 포함)
            ExternalArtworkResponse artwork = externalApiService.getArtworkById(request.getArtworkId());
            if (artwork == null) {
                throw new RuntimeException("작품을 찾을 수 없습니다: " + request.getArtworkId());
            }
            
            log.info("작품 정보 조회 성공 - artworkId: {}, title: {}", artwork.getId(), artwork.getTitle());
            
            // 2. 작품 사진으로 포토카드 생성
            PhotocardResponse response = createPhotocardWithArtwork(request, artwork);
            
            return response;
        } catch (Exception e) {
            log.error("포토카드 생성 중 오류 발생 - artworkId: {}", request.getArtworkId(), e);
            throw new RuntimeException("포토카드 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 작품 사진으로 포토카드 생성
     */
    private PhotocardResponse createPhotocardWithArtwork(PhotocardCreateRequest request, 
                                                         ExternalArtworkResponse artwork) {
        try {
            log.info("작품 사진으로 포토카드 생성 시작 - artworkId: {}", request.getArtworkId());
            
            // 1. 작품 사진으로 포토카드 이미지 생성
            byte[] photocardImage = imageProcessingService.generatePhotocardImage(artwork, null);
            
            // 2. Azure Storage에 파일 저장
            String fileId = azureStorageService.savePhotocardImage(photocardImage);
            
            // 3. 포토카드 엔티티 생성
            Photocard photocard = Photocard.builder()
                    .artworkId(request.getArtworkId())
                    .downloadUrl(azureStorageService.generateDownloadUrl(fileId))
                    .build();
            
            // 4. 데이터베이스 저장
            Photocard savedPhotocard = photocardRepository.save(photocard);
            
            log.info("포토카드 생성 완료 - id: {}, fileId: {}", savedPhotocard.getId(), fileId);
            
            return PhotocardResponse.from(savedPhotocard);
            
        } catch (Exception e) {
            log.error("포토카드 생성 실패", e);
            throw new RuntimeException("포토카드 생성에 실패했습니다: " + e.getMessage());
        }
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
    public List<PhotocardResponse> getPhotocardsByArtworkId(Long artworkId) {
        List<Photocard> photocards = photocardRepository.findByArtworkId(artworkId);
        return photocards.stream()
                .map(PhotocardResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 작품 선택 처리 (Chat-Orchestra에서 호출)
     */
    public PhotocardResponse selectArtwork(Long artworkId) {
        log.info("작품 선택 처리 - artworkId: {}", artworkId);
        
        // 1. 이미 해당 작품으로 포토카드가 생성되었는지 확인
        return photocardRepository.findByArtworkId(artworkId)
                .stream()
                .findFirst()
                .map(PhotocardResponse::from)
                .orElseGet(() -> {
                    // 2. 새로운 포토카드 생성
                    PhotocardCreateRequest request = PhotocardCreateRequest.builder()
                            .artworkId(artworkId)
                            .build();
                    return createPhotocard(request);
                });
    }
    
    
    /**
     * MultipartFile로 실제 포토카드 생성
     */
    public PhotocardResponse createPhotocardWithFile(MultipartFile file, Long artworkId) {
        log.info("MultipartFile로 포토카드 생성 시작 - fileName: {}, size: {}, artworkId: {}", 
                file.getOriginalFilename(), file.getSize(), artworkId);
        
        try {
            // 1. MultipartFile에서 바이트 배열 추출
            byte[] imageBytes = file.getBytes();
            
            // 2. Azure Storage에 파일 저장
            String fileId = azureStorageService.savePhotocardImage(imageBytes);
            
            // 3. 포토카드 엔티티 생성 (데이터베이스 저장 없이)
            Photocard photocard = Photocard.builder()
                    .artworkId(artworkId)
                    .downloadUrl(azureStorageService.generateDownloadUrl(fileId))
                    .build();
            
            // 4. 데이터베이스 저장 시도 (실패해도 계속 진행)
            try {
                Photocard savedPhotocard = photocardRepository.save(photocard);
                log.info("MultipartFile 포토카드 생성 완료 - id: {}, fileId: {}", savedPhotocard.getId(), fileId);
                return PhotocardResponse.from(savedPhotocard);
            } catch (Exception dbException) {
                log.warn("데이터베이스 저장 실패, 파일만 저장하고 계속 진행 - fileId: {}", fileId);
                // 데이터베이스 저장 실패해도 파일은 저장되었으므로 응답 생성
                return PhotocardResponse.builder()
                        .id(999L) // 임시 ID
                        .downloadUrl(azureStorageService.generateDownloadUrl(fileId))
                        .createdAt(java.time.LocalDateTime.now())
                        .build();
            }
                    
        } catch (Exception e) {
            log.error("MultipartFile 포토카드 생성 실패", e);
            throw new RuntimeException("MultipartFile 포토카드 생성에 실패했습니다: " + e.getMessage());
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