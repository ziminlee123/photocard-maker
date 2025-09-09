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
    
    public static PhotocardResponse from(Photocard photocard) {
        return PhotocardResponse.builder()
                .id(photocard.getId())
                .artworkId(photocard.getArtworkId())
                .conversationId(photocard.getConversationId())
                .downloadUrl(photocard.getDownloadUrl())
                .createdAt(photocard.getCreatedAt())
                .build();
    }
}
