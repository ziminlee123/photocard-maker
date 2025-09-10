-- ===========================================
-- Photocard-Maker 서비스 최소 스키마
-- 실제 필요한 테이블만 생성
-- ===========================================

-- 1. 대화 테이블 (conversation_id 참조용)
-- ===========================================
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    INDEX idx_started_at (started_at)
);

-- 2. 포토카드 테이블 (ERD 정확히 맞춤)
-- ===========================================
CREATE TABLE IF NOT EXISTS photocards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    artwork_id VARCHAR(36) NOT NULL,  -- UUID as VARCHAR(36)
    download_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_artwork_id (artwork_id),
    INDEX idx_created_at (created_at)
);

-- 3. 작품 선택 기록 테이블
-- ===========================================
CREATE TABLE IF NOT EXISTS artwork_selections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    artwork_id VARCHAR(36) NOT NULL,  -- UUID as VARCHAR(36)
    selected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_artwork_id (artwork_id),
    INDEX idx_selected_at (selected_at)
);

-- ===========================================
-- 샘플 데이터 (테스트용)
-- ===========================================

-- 샘플 대화 데이터
INSERT INTO conversations (id, started_at) VALUES
(1, '2025-09-09 10:00:00'),
(2, '2025-09-09 11:00:00');

-- 샘플 작품 선택 데이터
INSERT INTO artwork_selections (conversation_id, artwork_id, selected_at) VALUES
(1, '660e8400-e29b-41d4-a716-446655440001', '2025-09-09 10:03:00'),
(1, '660e8400-e29b-41d4-a716-446655440002', '2025-09-09 10:04:00'),
(2, '660e8400-e29b-41d4-a716-446655440003', '2025-09-09 11:03:00');

-- 샘플 포토카드 데이터
INSERT INTO photocards (conversation_id, artwork_id, download_url, created_at) VALUES
(1, '660e8400-e29b-41d4-a716-446655440001', 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample1/download', '2025-09-09 10:05:00'),
(1, '660e8400-e29b-41d4-a716-446655440002', 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample2/download', '2025-09-09 10:06:00'),
(2, '660e8400-e29b-41d4-a716-446655440003', 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample3/download', '2025-09-09 11:05:00');

-- ===========================================
-- 뷰 생성 (자주 사용되는 조인 쿼리용)
-- ===========================================

-- 대화별 포토카드 통계 뷰
CREATE VIEW conversation_photocard_stats AS
SELECT 
    c.id as conversation_id,
    c.started_at,
    COUNT(DISTINCT asel.artwork_id) as selected_artworks_count,
    COUNT(DISTINCT p.id) as photocards_created
FROM conversations c
LEFT JOIN artwork_selections asel ON c.id = asel.conversation_id
LEFT JOIN photocards p ON c.id = p.conversation_id
GROUP BY c.id, c.started_at;

-- ===========================================
-- 완료 메시지
-- ===========================================

SELECT 'Photocard-Maker 서비스 최소 스키마 생성 완료!' as message;
