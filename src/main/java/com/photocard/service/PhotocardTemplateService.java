package com.photocard.service;

import com.photocard.entity.PhotocardTemplate;
import com.photocard.repository.PhotocardTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PhotocardTemplateService {
    
    private final PhotocardTemplateRepository templateRepository;
    
    /**
     * 모든 활성화된 템플릿 조회
     */
    @Transactional(readOnly = true)
    public List<PhotocardTemplate> getAllActiveTemplates() {
        log.info("활성화된 모든 템플릿 조회");
        return templateRepository.findByIsActiveTrue();
    }
    
    /**
     * 타입별 활성화된 템플릿 조회
     */
    @Transactional(readOnly = true)
    public List<PhotocardTemplate> getActiveTemplatesByType(PhotocardTemplate.TemplateType type) {
        log.info("타입별 활성화된 템플릿 조회 - type: {}", type);
        return templateRepository.findByTypeAndIsActiveTrue(type);
    }
    
    /**
     * 템플릿 ID로 조회
     */
    @Transactional(readOnly = true)
    public Optional<PhotocardTemplate> getTemplateById(Long id) {
        log.info("템플릿 ID로 조회 - id: {}", id);
        return templateRepository.findById(id);
    }
    
    /**
     * 템플릿 이름으로 조회
     */
    @Transactional(readOnly = true)
    public Optional<PhotocardTemplate> getTemplateByName(String name) {
        log.info("템플릿 이름으로 조회 - name: {}", name);
        return templateRepository.findByNameAndIsActiveTrue(name);
    }
    
    /**
     * 기본 템플릿 조회 (첫 번째 활성화된 템플릿)
     */
    @Transactional(readOnly = true)
    public PhotocardTemplate getDefaultTemplate() {
        log.info("기본 템플릿 조회");
        List<PhotocardTemplate> activeTemplates = templateRepository.findByIsActiveTrue();
        
        if (activeTemplates.isEmpty()) {
            // 기본 템플릿이 없으면 생성
            return createDefaultTemplate();
        }
        
        return activeTemplates.get(0);
    }
    
    /**
     * 템플릿 생성
     */
    public PhotocardTemplate createTemplate(PhotocardTemplate template) {
        log.info("템플릿 생성 - name: {}", template.getName());
        
        // 이름 중복 확인
        if (templateRepository.findByName(template.getName()).isPresent()) {
            throw new RuntimeException("이미 존재하는 템플릿 이름입니다: " + template.getName());
        }
        
        PhotocardTemplate savedTemplate = templateRepository.save(template);
        log.info("템플릿 생성 완료 - id: {}", savedTemplate.getId());
        
        return savedTemplate;
    }
    
    /**
     * 템플릿 업데이트
     */
    public PhotocardTemplate updateTemplate(Long id, PhotocardTemplate template) {
        log.info("템플릿 업데이트 - id: {}", id);
        
        PhotocardTemplate existingTemplate = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + id));
        
        // 이름이 변경된 경우 중복 확인
        if (!existingTemplate.getName().equals(template.getName()) && 
            templateRepository.findByName(template.getName()).isPresent()) {
            throw new RuntimeException("이미 존재하는 템플릿 이름입니다: " + template.getName());
        }
        
        existingTemplate.setName(template.getName());
        existingTemplate.setDescription(template.getDescription());
        existingTemplate.setTemplateImageUrl(template.getTemplateImageUrl());
        existingTemplate.setWidth(template.getWidth());
        existingTemplate.setHeight(template.getHeight());
        existingTemplate.setType(template.getType());
        existingTemplate.setIsActive(template.getIsActive());
        existingTemplate.setLayoutConfig(template.getLayoutConfig());
        
        PhotocardTemplate updatedTemplate = templateRepository.save(existingTemplate);
        log.info("템플릿 업데이트 완료 - id: {}", updatedTemplate.getId());
        
        return updatedTemplate;
    }
    
    /**
     * 템플릿 삭제 (비활성화)
     */
    public void deleteTemplate(Long id) {
        log.info("템플릿 삭제 - id: {}", id);
        
        PhotocardTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + id));
        
        template.setIsActive(false);
        templateRepository.save(template);
        
        log.info("템플릿 삭제 완료 - id: {}", id);
    }
    
    /**
     * 기본 템플릿 생성 (시스템 초기화용)
     */
    private PhotocardTemplate createDefaultTemplate() {
        log.info("기본 템플릿 생성");
        
        PhotocardTemplate defaultTemplate = PhotocardTemplate.builder()
                .name("기본 템플릿")
                .description("시스템 기본 포토카드 템플릿")
                .templateImageUrl("classpath:/templates/default-template.png")
                .width(800)
                .height(600)
                .type(PhotocardTemplate.TemplateType.CLASSIC)
                .isActive(true)
                .layoutConfig(createDefaultLayoutConfig())
                .build();
        
        return templateRepository.save(defaultTemplate);
    }
    
    /**
     * 기본 레이아웃 설정 생성
     */
    private String createDefaultLayoutConfig() {
        // 실제로는 JSON으로 직렬화해야 함
        return "{\"textAreas\":[{\"id\":\"title\",\"x\":50,\"y\":50,\"width\":700,\"height\":60,\"fontFamily\":\"Arial\",\"fontSize\":24,\"fontColor\":\"#000000\",\"alignment\":\"CENTER\"},{\"id\":\"description\",\"x\":50,\"y\":120,\"width\":700,\"height\":100,\"fontFamily\":\"Arial\",\"fontSize\":16,\"fontColor\":\"#666666\",\"alignment\":\"CENTER\"}],\"imageAreas\":[{\"id\":\"artwork\",\"x\":100,\"y\":250,\"width\":600,\"height\":300,\"fitMode\":\"COVER\",\"alignment\":\"CENTER\"}],\"background\":{\"type\":\"COLOR\",\"color\":\"#FFFFFF\",\"opacity\":1.0}}";
    }
}
