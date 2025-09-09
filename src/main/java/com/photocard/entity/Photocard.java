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
    private String sessionId; //conversationId
    
    @Column(nullable = false)
    private String title; //
    
    @Column(columnDefinition = "TEXT")
    private String description; //삭제제
    
    @Column
    private Long endingCreditId;
    
    @Column(columnDefinition = "TEXT")
    private String conversationSummary;
    
    @Column(columnDefinition = "TEXT")
    private String artworkMetadata; //삭제제
    
    @Column(columnDefinition = "TEXT")
    private String endingCreditMetadata; //삭삭
    
    @Column(columnDefinition = "TEXT")
    private String combinedMetadata; //삭삭
    
    @Column(nullable = false)
    private String previewUrl; //삭삭
    
    @Column(nullable = false)
    private String downloadUrl; 
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhotocardStatus status; //
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum PhotocardStatus {
        GENERATING, COMPLETED, FAILED
    }
}
