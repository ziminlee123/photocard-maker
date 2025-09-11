package com.photocard.dto;

import com.photocard.entity.Photocard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포토카드 응답")
public class PhotocardResponse {
    
    @Schema(description = "포토카드 ID", example = "1")
    private Long id;
    
    @Schema(description = "작품 ID", example = "1")
    private Long artworkId;
    
    @Schema(description = "다운로드 URL", example = "https://example.com/photocard/123.jpg")
    private String downloadUrl;
    
    @Schema(description = "생성일시", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;
    
    public static PhotocardResponse from(Photocard photocard) {
        return PhotocardResponse.builder()
                .id(photocard.getId())
                .artworkId(photocard.getArtworkId())
                .downloadUrl(photocard.getDownloadUrl())
                .createdAt(photocard.getCreatedAt())
                .build();
    }
}
