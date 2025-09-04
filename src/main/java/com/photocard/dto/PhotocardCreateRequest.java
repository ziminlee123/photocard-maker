package com.photocard.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotocardCreateRequest {
    
    @NotNull(message = "작품 ID는 필수입니다")
    private Long artworkId;
    
    @NotNull(message = "세션 ID는 필수입니다")
    private String sessionId;
    
    private String title;
    private String description;
}
