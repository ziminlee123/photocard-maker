package com.photocard.service;

import com.photocard.dto.EndingCreditResponse;
import com.photocard.dto.ExternalArtworkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataCombinationService {
    
    /**
     * 작품 정보와 엔딩크레딧을 조합하여 포토카드 메타데이터 생성
     */
    public PhotocardMetadata combineMetadata(
            ExternalArtworkResponse artwork,
            EndingCreditResponse endingCredit,
            String sessionId) {
        
        log.info("메타데이터 조합 시작 - sessionId: {}, artworkId: {}, endingCreditId: {}", 
                sessionId, artwork.getId(), endingCredit.getId());
        
        // 개별 메타데이터 저장
        String artworkMetadata = artwork.getMetadata();
        String endingCreditMetadata = endingCredit.getMetadata();
        
        // 조합된 메타데이터 생성
        Map<String, Object> combinedData = new HashMap<>();
        combinedData.put("sessionId", sessionId);
        combinedData.put("artwork", Map.of(
                "id", artwork.getId(),
                "title", artwork.getTitle(),
                "artist", artwork.getArtist(),
                "exhibitionId", artwork.getExhibitionId(),
                "exhibitionTitle", artwork.getExhibitionTitle(),
                "metadata", artworkMetadata
        ));
        combinedData.put("endingCredit", Map.of(
                "id", endingCredit.getId(),
                "conversationSummary", endingCredit.getConversationSummary(),
                "participants", endingCredit.getParticipants(),
                "duration", endingCredit.getDuration(),
                "metadata", endingCreditMetadata
        ));
        combinedData.put("combinedAt", System.currentTimeMillis());
        
        String combinedMetadata = convertToJson(combinedData);
        
        return PhotocardMetadata.builder()
                .endingCreditId(endingCredit.getId())
                .conversationSummary(endingCredit.getConversationSummary())
                .artworkMetadata(artworkMetadata)
                .endingCreditMetadata(endingCreditMetadata)
                .combinedMetadata(combinedMetadata)
                .build();
    }
    
    /**
     * Map을 JSON 문자열로 변환 (간단한 구현)
     */
    private String convertToJson(Map<String, Object> data) {
        // 실제로는 Jackson ObjectMapper를 사용해야 함
        // 여기서는 간단한 문자열로 변환
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else if (entry.getValue() instanceof Map) {
                json.append(convertMapToJson((Map<?, ?>) entry.getValue()));
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Map을 JSON으로 변환하는 헬퍼 메서드
     */
    private String convertMapToJson(Map<?, ?> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * 포토카드 메타데이터 DTO
     */
    public static class PhotocardMetadata {
        private Long endingCreditId;
        private String conversationSummary;
        private String artworkMetadata;
        private String endingCreditMetadata;
        private String combinedMetadata;
        
        public static PhotocardMetadataBuilder builder() {
            return new PhotocardMetadataBuilder();
        }
        
        // Getters and Setters
        public Long getEndingCreditId() { return endingCreditId; }
        public void setEndingCreditId(Long endingCreditId) { this.endingCreditId = endingCreditId; }
        
        public String getConversationSummary() { return conversationSummary; }
        public void setConversationSummary(String conversationSummary) { this.conversationSummary = conversationSummary; }
        
        public String getArtworkMetadata() { return artworkMetadata; }
        public void setArtworkMetadata(String artworkMetadata) { this.artworkMetadata = artworkMetadata; }
        
        public String getEndingCreditMetadata() { return endingCreditMetadata; }
        public void setEndingCreditMetadata(String endingCreditMetadata) { this.endingCreditMetadata = endingCreditMetadata; }
        
        public String getCombinedMetadata() { return combinedMetadata; }
        public void setCombinedMetadata(String combinedMetadata) { this.combinedMetadata = combinedMetadata; }
        
        public static class PhotocardMetadataBuilder {
            private Long endingCreditId;
            private String conversationSummary;
            private String artworkMetadata;
            private String endingCreditMetadata;
            private String combinedMetadata;
            
            public PhotocardMetadataBuilder endingCreditId(Long endingCreditId) {
                this.endingCreditId = endingCreditId;
                return this;
            }
            
            public PhotocardMetadataBuilder conversationSummary(String conversationSummary) {
                this.conversationSummary = conversationSummary;
                return this;
            }
            
            public PhotocardMetadataBuilder artworkMetadata(String artworkMetadata) {
                this.artworkMetadata = artworkMetadata;
                return this;
            }
            
            public PhotocardMetadataBuilder endingCreditMetadata(String endingCreditMetadata) {
                this.endingCreditMetadata = endingCreditMetadata;
                return this;
            }
            
            public PhotocardMetadataBuilder combinedMetadata(String combinedMetadata) {
                this.combinedMetadata = combinedMetadata;
                return this;
            }
            
            public PhotocardMetadata build() {
                PhotocardMetadata metadata = new PhotocardMetadata();
                metadata.setEndingCreditId(this.endingCreditId);
                metadata.setConversationSummary(this.conversationSummary);
                metadata.setArtworkMetadata(this.artworkMetadata);
                metadata.setEndingCreditMetadata(this.endingCreditMetadata);
                metadata.setCombinedMetadata(this.combinedMetadata);
                return metadata;
            }
        }
    }
}
