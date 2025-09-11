package com.photocard.service;

import com.photocard.config.ExternalApiConfig;
//import com.photocard.dto.EndingCreditResponse;
import com.photocard.dto.ExternalArtworkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiService {
    
    private final RestTemplate restTemplate;
    private final ExternalApiConfig apiConfig;
    햣햣
    /**
     * Exhibition 서비스에서 작품 정보 조회
     */
    public ExternalArtworkResponse getArtworkById(Long artworkId) {
        try {
            String url = apiConfig.getExhibitionBaseUrl() + "/api/artworks/" + artworkId;
            log.info("Exhibition 서비스에서 작품 조회: {}", url);
            
            return restTemplate.getForObject(url, ExternalArtworkResponse.class);
        } catch (Exception e) {
            log.error("작품 정보 조회 실패 - artworkId: {}, Mock 데이터 사용", artworkId, e);
            
            // 테스트용 Mock 데이터 반환
            return ExternalArtworkResponse.builder()
                    .id(artworkId)
                    .title("테스트 작품 " + artworkId)
                    .description("테스트용 작품 설명입니다")
                    .artist("테스트 작가")
                    .imageUrl("https://example.com/test-image.jpg")
                    .licenseInfo("테스트 라이선스")
                    .exhibitionId(1L)
                    .exhibitionTitle("테스트 전시회")
                    .metadata("{\"test\": \"mock data\"}")
                    .build();
        }
    }
    
    
    /**
     * 이미지 라이선스 확인 (외부 API 호출)
     */
    public boolean checkImageLicense(String imageUrl) {
        try {
            // 실제로는 외부 라이선스 API를 호출해야 함
            // 여기서는 간단히 true 반환
            log.info("이미지 라이선스 확인: {}", imageUrl);
            return true;
        } catch (Exception e) {
            log.error("이미지 라이선스 확인 실패: {}", imageUrl, e);
            return false;
        }
    }
}
