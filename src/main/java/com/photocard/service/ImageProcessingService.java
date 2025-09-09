package com.photocard.service;

import com.photocard.entity.Photocard;
import com.photocard.entity.PhotocardTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.imgscalr.Scalr; // 라이브러리 문제로 주석 처리
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingService {
    
    // private final FileStorageService fileStorageService; // 현재 사용하지 않음
    
    /**
     * 포토카드 이미지 생성
     */
    public byte[] generatePhotocardImage(Photocard photocard, PhotocardTemplate template) {
        log.info("포토카드 이미지 생성 시작 - photocardId: {}, templateId: {}", 
                photocard.getId(), template.getId());
        
        try {
            // 1. 템플릿 이미지 로드
            BufferedImage templateImage = loadTemplateImage(template);
            
            // 2. 작품 이미지 로드
            BufferedImage artworkImage = loadArtworkImage(photocard);
            
            // 3. 포토카드 생성
            BufferedImage photocardImage = createPhotocardImage(templateImage, artworkImage, photocard, template);
            
            // 4. 이미지를 바이트 배열로 변환
            byte[] imageBytes = convertImageToBytes(photocardImage, "jpg");
            
            log.info("포토카드 이미지 생성 완료 - photocardId: {}, size: {} bytes", 
                    photocard.getId(), imageBytes.length);
            
            return imageBytes;
            
        } catch (Exception e) {
            log.error("포토카드 이미지 생성 실패 - photocardId: {}", photocard.getId(), e);
            throw new RuntimeException("포토카드 이미지 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 템플릿 이미지 로드
     */
    private BufferedImage loadTemplateImage(PhotocardTemplate template) {
        try {
            if (template.getTemplateImageUrl().startsWith("classpath:")) {
                // 클래스패스 리소스 로드
                String resourcePath = template.getTemplateImageUrl().substring("classpath:".length());
                return ImageIO.read(getClass().getResourceAsStream(resourcePath));
            } else {
                // URL에서 로드
                return ImageIO.read(new URL(template.getTemplateImageUrl()));
            }
        } catch (IOException e) {
            log.warn("템플릿 이미지 로드 실패, 기본 이미지 생성 - templateId: {}", template.getId());
            return createDefaultTemplateImage(template.getWidth(), template.getHeight());
        }
    }
    
    /**
     * 작품 이미지 로드
     */
    private BufferedImage loadArtworkImage(Photocard photocard) {
        try {
            // 실제로는 ExternalApiService에서 작품 이미지 URL을 가져와야 함
            // 여기서는 더미 이미지 생성
            return createDummyArtworkImage(400, 300);
        } catch (Exception e) {
            log.warn("작품 이미지 로드 실패, 더미 이미지 생성 - photocardId: {}", photocard.getId());
            return createDummyArtworkImage(400, 300);
        }
    }
    
    /**
     * 포토카드 이미지 생성
     */
    private BufferedImage createPhotocardImage(BufferedImage templateImage, BufferedImage artworkImage, 
                                             Photocard photocard, PhotocardTemplate template) {
        // 1. 템플릿 이미지를 포토카드 크기로 리사이즈
        BufferedImage resizedTemplate = resizeImage(templateImage, 
                template.getWidth(), template.getHeight());
        
        // 2. 포토카드 캔버스 생성
        BufferedImage photocardImage = new BufferedImage(
                template.getWidth(), template.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = photocardImage.createGraphics();
        
        // 3. 템플릿 배경 그리기
        g2d.drawImage(resizedTemplate, 0, 0, null);
        
        // 4. 작품 이미지 오버레이
        drawArtworkImage(g2d, artworkImage, template);
        
        // 5. 텍스트 오버레이
        drawTextOverlay(g2d, photocard, template);
        
        g2d.dispose();
        
        return photocardImage;
    }
    
    /**
     * 작품 이미지 오버레이
     */
    private void drawArtworkImage(Graphics2D g2d, BufferedImage artworkImage, PhotocardTemplate template) {
        // 기본 위치와 크기 (실제로는 레이아웃 설정에서 가져와야 함)
        int x = 100;
        int y = 200;
        int width = 600;
        int height = 300;
        
        // 작품 이미지 리사이즈
        BufferedImage resizedArtwork = resizeImage(artworkImage, width, height);
        
        // 작품 이미지 그리기
        g2d.drawImage(resizedArtwork, x, y, null);
    }
    
    /**
     * 텍스트 오버레이
     */
    private void drawTextOverlay(Graphics2D g2d, Photocard photocard, PhotocardTemplate template) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // 제목 그리기
        drawTitle(g2d, photocard, template);
        
        // 설명 그리기
        drawDescription(g2d, photocard, template);
        
        // 대화 요약 그리기
        drawConversationSummary(g2d, photocard, template);
    }
    
    /**
     * 제목 그리기
     */
    private void drawTitle(Graphics2D g2d, Photocard photocard, PhotocardTemplate template) {
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(Color.BLACK);
        
        String title = photocard.getTitle();
        if (title != null && !title.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (template.getWidth() - fm.stringWidth(title)) / 2;
            int y = 50;
            
            g2d.drawString(title, x, y);
        }
    }
    
    /**
     * 설명 그리기
     */
    private void drawDescription(Graphics2D g2d, Photocard photocard, PhotocardTemplate template) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(102, 102, 102));
        
        String description = photocard.getDescription();
        if (description != null && !description.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (template.getWidth() - fm.stringWidth(description)) / 2;
            int y = 80;
            
            g2d.drawString(description, x, y);
        }
    }
    
    /**
     * 대화 요약 그리기
     */
    private void drawConversationSummary(Graphics2D g2d, Photocard photocard, PhotocardTemplate template) {
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        g2d.setColor(new Color(153, 153, 153));
        
        String summary = photocard.getConversationSummary();
        if (summary != null && !summary.isEmpty()) {
            // 긴 텍스트는 여러 줄로 나누기
            String[] lines = wrapText(summary, 60);
            FontMetrics fm = g2d.getFontMetrics();
            int lineHeight = fm.getHeight();
            int startY = template.getHeight() - 100;
            
            for (int i = 0; i < lines.length && i < 3; i++) {
                int x = (template.getWidth() - fm.stringWidth(lines[i])) / 2;
                int y = startY + (i * lineHeight);
                g2d.drawString(lines[i], x, y);
            }
        }
    }
    
    /**
     * 텍스트 줄바꿈
     */
    private String[] wrapText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return new String[]{text};
        }
        
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= maxLength) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    /**
     * 기본 템플릿 이미지 생성
     */
    private BufferedImage createDefaultTemplateImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 흰색 배경
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // 테두리
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(10, 10, width - 20, height - 20);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * 더미 작품 이미지 생성
     */
    private BufferedImage createDummyArtworkImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 그라데이션 배경
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(135, 206, 250),
                width, height, new Color(255, 182, 193));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // 중앙에 텍스트
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "작품 이미지";
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height + fm.getHeight()) / 2;
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * 이미지를 바이트 배열로 변환
     */
    private byte[] convertImageToBytes(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }
    
    /**
     * 이미지 리사이즈
     */
    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        
        // 고품질 렌더링 설정
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 이미지 그리기
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        
        return resizedImage;
    }
    
    /**
     * 이미지 최적화 (품질 조정)
     */
    public BufferedImage optimizeImage(BufferedImage image, float quality) {
        // 이미지 품질 최적화 로직 (현재는 원본 반환)
        return image;
    }
}
