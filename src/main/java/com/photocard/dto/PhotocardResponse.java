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
    private String sessionId;
    private String title;
    private String description;
    private String previewUrl;
    private String downloadUrl;
    private Photocard.PhotocardStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static PhotocardResponse from(Photocard photocard) {
        return PhotocardResponse.builder()
                .id(photocard.getId())
                .artworkId(photocard.getArtworkId())
                .sessionId(photocard.getSessionId())
                .title(photocard.getTitle())
                .description(photocard.getDescription())
                .previewUrl(photocard.getPreviewUrl())
                .downloadUrl(photocard.getDownloadUrl())
                .status(photocard.getStatus())
                .createdAt(photocard.getCreatedAt())
                .updatedAt(photocard.getUpdatedAt())
                .build();
    }
}
