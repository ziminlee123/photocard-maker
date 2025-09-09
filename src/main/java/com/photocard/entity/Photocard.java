package com.photocard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "photocards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photocard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long artworkId;
    
    @Column(nullable = false)
    private String sessionId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private Long endingCreditId;
    
    @Column(columnDefinition = "TEXT")
    private String conversationSummary;
    
    @Column(columnDefinition = "TEXT")
    private String artworkMetadata;
    
    @Column(columnDefinition = "TEXT")
    private String endingCreditMetadata;
    
    @Column(columnDefinition = "TEXT")
    private String combinedMetadata;
    
    @Column(nullable = false)
    private String previewUrl;
    
    @Column(nullable = false)
    private String downloadUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhotocardStatus status;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum PhotocardStatus {
        GENERATING, COMPLETED, FAILED
    }
}
