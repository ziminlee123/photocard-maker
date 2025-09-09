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
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
}
