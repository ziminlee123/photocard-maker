package com.photocard.repository;

import com.photocard.entity.ArtworkSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkSelectionRepository extends JpaRepository<ArtworkSelection, Long> {
    
    List<ArtworkSelection> findByConversationId(Long conversationId);
    
    boolean existsByConversationIdAndArtworkId(Long conversationId, Long artworkId);
}
