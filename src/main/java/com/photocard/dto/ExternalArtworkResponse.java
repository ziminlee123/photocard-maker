package com.photocard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalArtworkResponse {
    
    private Long id;
    private String title;
    private String description;
    private String artist;
    private String imageUrl;
    private String licenseInfo;
    private Long exhibitionId;
    private String exhibitionTitle;
    private String metadata;
}
