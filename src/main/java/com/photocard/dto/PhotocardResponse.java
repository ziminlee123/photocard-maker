package com.photocard.dto;

import com.photocard.entity.Photocard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotocardResponse {
    
    private Long id;
    private Long artworkId;
    private String conversationId;
    private String downloadUrl;
    private LocalDateTime createdAt;
    
    // 호환성을 위한 필드들
    private String sessionId; // conversationId와 동일
    private String title; // 작품 제목
    private String description; // 작품 설명
    private String previewUrl; // downloadUrl과 동일
    private String status = "COMPLETED"; // 기본값
    
    public static PhotocardResponse from(Photocard photocard) {
        return PhotocardResponse.builder()
                .id(photocard.getId())
                .artworkId(photocard.getArtworkId())
                .conversationId(photocard.getConversationId())
                .downloadUrl(photocard.getDownloadUrl())
                .createdAt(photocard.getCreatedAt())
                // 호환성 필드들
                .sessionId(photocard.getConversationId()) // conversationId와 동일
                .title("포토카드") // 기본 제목
                .description("작품으로 만든 포토카드입니다") // 기본 설명
                .previewUrl(photocard.getDownloadUrl()) // downloadUrl과 동일
                .status("COMPLETED") // 기본 상태
                .build();
    }
}
