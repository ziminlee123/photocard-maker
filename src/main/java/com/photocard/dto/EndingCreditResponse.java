package com.photocard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndingCreditResponse {
    
    private Long id;
    private Long sessionId;
    private List<Long> artworkIds;
    private String conversationSummary;
    private String participants;
    private String duration;
    private String metadata;
    private LocalDateTime createdAt;
}
