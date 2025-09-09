package com.photocard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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
    
    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "artwork_id", nullable = false)
    private Long artworkId;

    @Column(name = "download_url", columnDefinition = "TEXT", nullable = false)
    private String downloadUrl;
    
    // 임시 호환성을 위한 필드들 (DB에 없어도 됨)
    @Transient
    private String sessionId; // conversationId와 동일한 값
    
    @Transient
    private String title; // 작품 제목
    
    @Transient
    private String description; // 작품 설명
    
    @Transient
    private String previewUrl; // downloadUrl과 동일
    
    @Transient
    private String status = "COMPLETED"; // 기본값
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
}
