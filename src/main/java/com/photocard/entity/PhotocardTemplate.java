package com.photocard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "photocard_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotocardTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String templateImageUrl;
    
    @Column(nullable = false)
    private Integer width;
    
    @Column(nullable = false)
    private Integer height;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateType type;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column(columnDefinition = "TEXT")
    private String layoutConfig;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum TemplateType {
        CLASSIC,    // 클래식 스타일
        MODERN,     // 모던 스타일
        MINIMAL,    // 미니멀 스타일
        ARTISTIC,   // 아티스틱 스타일
        CUSTOM      // 커스텀 스타일
    }
    
    /**
     * 템플릿 레이아웃 설정을 위한 내부 클래스
     */
    public static class LayoutConfig {
        private List<TextArea> textAreas;
        private List<ImageArea> imageAreas;
        private BackgroundConfig background;
        
        // Getters and Setters
        public List<TextArea> getTextAreas() { return textAreas; }
        public void setTextAreas(List<TextArea> textAreas) { this.textAreas = textAreas; }
        
        public List<ImageArea> getImageAreas() { return imageAreas; }
        public void setImageAreas(List<ImageArea> imageAreas) { this.imageAreas = imageAreas; }
        
        public BackgroundConfig getBackground() { return background; }
        public void setBackground(BackgroundConfig background) { this.background = background; }
    }
    
    /**
     * 텍스트 영역 설정
     */
    public static class TextArea {
        private String id;
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
        private String fontFamily;
        private Integer fontSize;
        private String fontColor;
        private String backgroundColor;
        private String alignment; // LEFT, CENTER, RIGHT
        private String verticalAlignment; // TOP, MIDDLE, BOTTOM
        private Integer maxLength;
        private String content;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public Integer getX() { return x; }
        public void setX(Integer x) { this.x = x; }
        
        public Integer getY() { return y; }
        public void setY(Integer y) { this.y = y; }
        
        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }
        
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
        
        public String getFontFamily() { return fontFamily; }
        public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }
        
        public Integer getFontSize() { return fontSize; }
        public void setFontSize(Integer fontSize) { this.fontSize = fontSize; }
        
        public String getFontColor() { return fontColor; }
        public void setFontColor(String fontColor) { this.fontColor = fontColor; }
        
        public String getBackgroundColor() { return backgroundColor; }
        public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }
        
        public String getAlignment() { return alignment; }
        public void setAlignment(String alignment) { this.alignment = alignment; }
        
        public String getVerticalAlignment() { return verticalAlignment; }
        public void setVerticalAlignment(String verticalAlignment) { this.verticalAlignment = verticalAlignment; }
        
        public Integer getMaxLength() { return maxLength; }
        public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    /**
     * 이미지 영역 설정
     */
    public static class ImageArea {
        private String id;
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
        private String imageUrl;
        private String fitMode; // COVER, CONTAIN, FILL
        private String alignment; // LEFT, CENTER, RIGHT
        private String verticalAlignment; // TOP, MIDDLE, BOTTOM
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public Integer getX() { return x; }
        public void setX(Integer x) { this.x = x; }
        
        public Integer getY() { return y; }
        public void setY(Integer y) { this.y = y; }
        
        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }
        
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getFitMode() { return fitMode; }
        public void setFitMode(String fitMode) { this.fitMode = fitMode; }
        
        public String getAlignment() { return alignment; }
        public void setAlignment(String alignment) { this.alignment = alignment; }
        
        public String getVerticalAlignment() { return verticalAlignment; }
        public void setVerticalAlignment(String verticalAlignment) { this.verticalAlignment = verticalAlignment; }
    }
    
    /**
     * 배경 설정
     */
    public static class BackgroundConfig {
        private String type; // COLOR, IMAGE, GRADIENT
        private String color;
        private String imageUrl;
        private String gradientConfig;
        private Double opacity;
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getGradientConfig() { return gradientConfig; }
        public void setGradientConfig(String gradientConfig) { this.gradientConfig = gradientConfig; }
        
        public Double getOpacity() { return opacity; }
        public void setOpacity(Double opacity) { this.opacity = opacity; }
    }
}
