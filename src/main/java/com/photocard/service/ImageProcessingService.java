package com.photocard.service;

import com.photocard.dto.ExternalArtworkResponse;
import com.photocard.dto.EndingCreditResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    
    /**
     * 간단한 포토카드 이미지 생성 (메타데이터 없이)
     */
    public byte[] generateSimplePhotocardImage(ExternalArtworkResponse artwork) {
        log.info("간단한 포토카드 이미지 생성 시작 - artworkId: {}", artwork.getId());
        
        try {
            // 1. 작품 이미지 로드
            BufferedImage artworkImage = loadArtworkImage(artwork);
            
            // 2. 포토카드 크기 설정 (600x400)
            int cardWidth = 600;
            int cardHeight = 400;
            BufferedImage photocard = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = photocard.createGraphics();
            
            // 3. 배경색 설정 (흰색)
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, cardWidth, cardHeight);
            
            // 4. 작품 이미지 그리기 (중앙에 배치)
            int imageWidth = 400;
            int imageHeight = 300;
            int x = (cardWidth - imageWidth) / 2;
            int y = (cardHeight - imageHeight) / 2;
            g2d.drawImage(artworkImage, x, y, imageWidth, imageHeight, null);
            
            // 5. 작품 제목 추가
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String title = artwork.getTitle() != null ? artwork.getTitle() : "작품 제목";
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = (cardWidth - fm.stringWidth(title)) / 2;
            g2d.drawString(title, titleX, 30);
            
            // 6. 작가명 추가
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String artist = artwork.getArtist() != null ? artwork.getArtist() : "작가명";
            fm = g2d.getFontMetrics();
            int artistX = (cardWidth - fm.stringWidth(artist)) / 2;
            g2d.drawString(artist, artistX, cardHeight - 20);
            
            g2d.dispose();
            
            // 7. 바이트 배열로 변환
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(photocard, "PNG", baos);
            
            log.info("간단한 포토카드 이미지 생성 완료 - 크기: {} bytes", baos.size());
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("간단한 포토카드 이미지 생성 실패", e);
            throw new RuntimeException("포토카드 이미지 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 포토카드 이미지 생성 (단순화된 버전)
     */
    public byte[] generatePhotocardImage(ExternalArtworkResponse artwork, EndingCreditResponse endingCredit) {
        log.info("포토카드 이미지 생성 시작 - artworkId: {}", artwork.getId());
        
        try {
            // 1. 작품 이미지 로드
            BufferedImage artworkImage = loadArtworkImage(artwork);
            
            // 2. 포토카드 생성 (기본 템플릿 사용)
            BufferedImage photocardImage = createPhotocardImage(artworkImage, artwork, endingCredit);
            
            // 3. 이미지를 바이트 배열로 변환
            byte[] imageBytes = convertImageToBytes(photocardImage, "jpg");
            
            log.info("포토카드 이미지 생성 완료 - artworkId: {}, size: {} bytes", 
                    artwork.getId(), imageBytes.length);
            
            return imageBytes;
            
        } catch (Exception e) {
            log.error("포토카드 이미지 생성 실패 - artworkId: {}", artwork.getId(), e);
            throw new RuntimeException("포토카드 이미지 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 작품 이미지 로드
     */
    private BufferedImage loadArtworkImage(ExternalArtworkResponse artwork) {
        try {
            if (artwork.getImageUrl() != null && !artwork.getImageUrl().isEmpty()) {
                return ImageIO.read(new URL(artwork.getImageUrl()));
            } else {
                log.warn("작품 이미지 URL이 없음, 더미 이미지 생성 - artworkId: {}", artwork.getId());
                return createDummyArtworkImage(400, 300);
            }
        } catch (Exception e) {
            log.warn("작품 이미지 로드 실패, 더미 이미지 생성 - artworkId: {}", artwork.getId());
            return createDummyArtworkImage(400, 300);
        }
    }
    
    /**
     * 포토카드 이미지 생성 (단순화된 버전)
     */
    private BufferedImage createPhotocardImage(BufferedImage artworkImage, 
                                             ExternalArtworkResponse artwork, 
                                             EndingCreditResponse endingCredit) {
        // 기본 포토카드 크기
        int width = 800;
        int height = 600;
        
        // 포토카드 캔버스 생성
        BufferedImage photocardImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = photocardImage.createGraphics();
        
        // 고품질 렌더링 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // 1. 배경 그리기
        drawBackground(g2d, width, height);
        
        // 2. 작품 이미지 오버레이
        drawArtworkImage(g2d, artworkImage, width, height);
        
        // 3. 텍스트 오버레이
        drawTextOverlay(g2d, artwork, endingCredit, width, height);
        
        g2d.dispose();
        
        return photocardImage;
    }
    
    /**
     * 배경 그리기
     */
    private void drawBackground(Graphics2D g2d, int width, int height) {
        // 흰색 배경
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // 테두리
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(10, 10, width - 20, height - 20);
    }
    
    /**
     * 작품 이미지 오버레이
     */
    private void drawArtworkImage(Graphics2D g2d, BufferedImage artworkImage, int width, int height) {
        // 작품 이미지 위치와 크기
        int x = 100;
        int y = 150;
        int imageWidth = 600;
        int imageHeight = 300;
        
        // 작품 이미지 리사이즈
        BufferedImage resizedArtwork = resizeImage(artworkImage, imageWidth, imageHeight);
        
        // 작품 이미지 그리기
        g2d.drawImage(resizedArtwork, x, y, null);
    }
    
    /**
     * 텍스트 오버레이
     */
    private void drawTextOverlay(Graphics2D g2d, ExternalArtworkResponse artwork, 
                                EndingCreditResponse endingCredit, int width, int height) {
        // 제목 그리기
        drawTitle(g2d, artwork, width);
        
        // 설명 그리기
        drawDescription(g2d, artwork, width);
        
        // 엔딩크레딧 그리기
        // drawEndingCredit(g2d, endingCredit, width, height);
    }
    
    /**
     * 제목 그리기
     */
    private void drawTitle(Graphics2D g2d, ExternalArtworkResponse artwork, int width) {
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(Color.BLACK);
        
        String title = artwork.getTitle();
        if (title != null && !title.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(title)) / 2;
            int y = 50;
            
            g2d.drawString(title, x, y);
        }
    }
    
    /**
     * 설명 그리기
     */
    private void drawDescription(Graphics2D g2d, ExternalArtworkResponse artwork, int width) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(102, 102, 102));
        
        String description = artwork.getDescription();
        if (description != null && !description.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(description)) / 2;
            int y = 80;
            
            g2d.drawString(description, x, y);
        }
    }
    
    /**
     * 엔딩크레딧 그리기
     */
    private void drawEndingCredit(Graphics2D g2d, EndingCreditResponse endingCredit, int width, int height) {
        // endingCredit이 null이면 그리지 않음
        if (endingCredit == null) {
            return;
        }
        
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        g2d.setColor(new Color(153, 153, 153));
        
        String content = endingCredit.getConversationSummary();
        if (content != null && !content.isEmpty()) {
            // 긴 텍스트는 여러 줄로 나누기
            String[] lines = wrapText(content, 60);
            FontMetrics fm = g2d.getFontMetrics();
            int lineHeight = fm.getHeight();
            int startY = height - 100;
            
            for (int i = 0; i < lines.length && i < 3; i++) {
                int x = (width - fm.stringWidth(lines[i])) / 2;
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
    
}
