package com.photocard.controller;

import com.photocard.entity.PhotocardTemplate;
import com.photocard.service.PhotocardTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Photocard Template", description = "포토카드 템플릿 관리 API")
public class PhotocardTemplateController {
    
    private final PhotocardTemplateService templateService;
    
    /**
     * 모든 활성화된 템플릿 조회
     * GET /api/templates
     */
    @Operation(summary = "활성화된 템플릿 목록 조회", description = "모든 활성화된 포토카드 템플릿을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "템플릿 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<PhotocardTemplate>> getAllActiveTemplates() {
        log.info("활성화된 템플릿 목록 조회 요청");
        
        try {
            List<PhotocardTemplate> templates = templateService.getAllActiveTemplates();
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("템플릿 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 타입별 활성화된 템플릿 조회
     * GET /api/templates/type/{type}
     */
    @Operation(summary = "타입별 템플릿 조회", description = "지정된 타입의 활성화된 템플릿을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "템플릿 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PhotocardTemplate>> getTemplatesByType(
            @Parameter(description = "템플릿 타입", required = true)
            @PathVariable PhotocardTemplate.TemplateType type) {
        
        log.info("타입별 템플릿 조회 요청 - type: {}", type);
        
        try {
            List<PhotocardTemplate> templates = templateService.getActiveTemplatesByType(type);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("타입별 템플릿 조회 실패 - type: {}", type, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 템플릿 ID로 조회
     * GET /api/templates/{id}
     */
    @Operation(summary = "템플릿 상세 조회", description = "템플릿 ID로 상세 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "템플릿 조회 성공"),
            @ApiResponse(responseCode = "404", description = "템플릿을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PhotocardTemplate> getTemplateById(
            @Parameter(description = "템플릿 ID", required = true)
            @PathVariable Long id) {
        
        log.info("템플릿 상세 조회 요청 - id: {}", id);
        
        try {
            Optional<PhotocardTemplate> template = templateService.getTemplateById(id);
            if (template.isPresent()) {
                return ResponseEntity.ok(template.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("템플릿 조회 실패 - id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 기본 템플릿 조회
     * GET /api/templates/default
     */
    @Operation(summary = "기본 템플릿 조회", description = "시스템 기본 템플릿을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기본 템플릿 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/default")
    public ResponseEntity<PhotocardTemplate> getDefaultTemplate() {
        log.info("기본 템플릿 조회 요청");
        
        try {
            PhotocardTemplate template = templateService.getDefaultTemplate();
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            log.error("기본 템플릿 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 템플릿 생성
     * POST /api/templates
     */
    @Operation(summary = "템플릿 생성", description = "새로운 포토카드 템플릿을 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "템플릿 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<PhotocardTemplate> createTemplate(
            @Parameter(description = "템플릿 정보", required = true)
            @RequestBody PhotocardTemplate template) {
        
        log.info("템플릿 생성 요청 - name: {}", template.getName());
        
        try {
            PhotocardTemplate createdTemplate = templateService.createTemplate(template);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
        } catch (Exception e) {
            log.error("템플릿 생성 실패 - name: {}", template.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 템플릿 업데이트
     * PUT /api/templates/{id}
     */
    @Operation(summary = "템플릿 수정", description = "기존 템플릿을 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "템플릿 수정 성공"),
            @ApiResponse(responseCode = "404", description = "템플릿을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PhotocardTemplate> updateTemplate(
            @Parameter(description = "템플릿 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "수정할 템플릿 정보", required = true)
            @RequestBody PhotocardTemplate template) {
        
        log.info("템플릿 수정 요청 - id: {}, name: {}", id, template.getName());
        
        try {
            PhotocardTemplate updatedTemplate = templateService.updateTemplate(id, template);
            return ResponseEntity.ok(updatedTemplate);
        } catch (Exception e) {
            log.error("템플릿 수정 실패 - id: {}, name: {}", id, template.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 템플릿 삭제 (비활성화)
     * DELETE /api/templates/{id}
     */
    @Operation(summary = "템플릿 삭제", description = "템플릿을 비활성화합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "템플릿 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "템플릿을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "템플릿 ID", required = true)
            @PathVariable Long id) {
        
        log.info("템플릿 삭제 요청 - id: {}", id);
        
        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("템플릿 삭제 실패 - id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
