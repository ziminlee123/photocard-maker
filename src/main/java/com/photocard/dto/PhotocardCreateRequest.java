package com.photocard.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포토카드 생성 요청")
public class PhotocardCreateRequest {
    
    @NotNull(message = "작품 ID는 필수입니다")
    @Schema(description = "작품 ID", example = "1", required = true)
    private Long artworkId;
}
