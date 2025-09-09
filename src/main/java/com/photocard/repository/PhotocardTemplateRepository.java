package com.photocard.repository;

import com.photocard.entity.PhotocardTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotocardTemplateRepository extends JpaRepository<PhotocardTemplate, Long> {
    
    /**
     * 활성화된 템플릿 목록 조회
     */
    List<PhotocardTemplate> findByIsActiveTrue();
    
    /**
     * 템플릿 타입별 활성화된 템플릿 목록 조회
     */
    List<PhotocardTemplate> findByTypeAndIsActiveTrue(PhotocardTemplate.TemplateType type);
    
    /**
     * 템플릿 이름으로 조회
     */
    Optional<PhotocardTemplate> findByName(String name);
    
    /**
     * 템플릿 이름으로 활성화된 템플릿 조회
     */
    Optional<PhotocardTemplate> findByNameAndIsActiveTrue(String name);
}
