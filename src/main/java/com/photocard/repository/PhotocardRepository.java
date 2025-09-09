package com.photocard.repository;

import com.photocard.entity.Photocard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotocardRepository extends JpaRepository<Photocard, Long> {
    
    List<Photocard> findBySessionId(String sessionId);
    
    Optional<Photocard> findBySessionIdAndArtworkId(String sessionId, Long artworkId);
    
    List<Photocard> findByStatus(Photocard.PhotocardStatus status);
}
