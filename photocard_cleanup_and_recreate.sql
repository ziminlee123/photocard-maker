-- ===========================================
-- Photocard-Maker 서비스 테이블 정리 및 재생성
-- 기존 테이블 삭제 후 새로운 스키마로 재생성
-- ===========================================

-- ===========================================
-- 1. 기존 테이블 정리 (외래키 제약조건 고려)
-- ===========================================

-- 외래키 제약조건 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 기존 테이블들 삭제 (의존성 순서대로)
DROP TABLE IF EXISTS word_frequency;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS rag_sources;
DROP TABLE IF EXISTS rag_responses;
DROP TABLE IF EXISTS photocards;
DROP TABLE IF EXISTS artwork_selections;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS ending_credits;
DROP TABLE IF EXISTS conversations;
DROP TABLE IF EXISTS artworks;
DROP TABLE IF EXISTS exhibitions;

-- 뷰 삭제
DROP VIEW IF EXISTS conversation_photocard_stats;
DROP VIEW IF EXISTS conversation_artwork_stats;
DROP VIEW IF EXISTS photocard_details;

-- 외래키 제약조건 재활성화
SET FOREIGN_KEY_CHECKS = 1;

-- ===========================================
-- 2. Photocard-Maker 서비스 최소 스키마 생성
-- ===========================================

-- 2-1. 대화 테이블 (conversation_id 참조용)
CREATE TABLE conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    INDEX idx_started_at (started_at)
);

-- 2-2. 포토카드 테이블 (ERD 정확히 맞춤)
CREATE TABLE photocards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    artwork_id BIGINT NOT NULL,  -- Long 타입 (Exhibition 서비스와 일치)
    download_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_artwork_id (artwork_id),
    INDEX idx_created_at (created_at)
);

-- 2-3. 작품 선택 기록 테이블
CREATE TABLE artwork_selections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    artwork_id BIGINT NOT NULL,  -- Long 타입 (Exhibition 서비스와 일치)
    selected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_artwork_id (artwork_id),
    INDEX idx_selected_at (selected_at)
);

-- ===========================================
-- 3. 샘플 데이터 삽입 (테스트용)
-- ===========================================

-- 샘플 대화 데이터
INSERT INTO conversations (id, started_at) VALUES
(1, '2025-09-09 10:00:00'),
(2, '2025-09-09 11:00:00'),
(3, '2025-09-09 12:00:00');

-- 샘플 작품 선택 데이터
INSERT INTO artwork_selections (conversation_id, artwork_id, selected_at) VALUES
(1, 1, '2025-09-09 10:03:00'),
(1, 2, '2025-09-09 10:04:00'),
(2, 3, '2025-09-09 11:03:00'),
(3, 1, '2025-09-09 12:03:00');

-- 샘플 포토카드 데이터
INSERT INTO photocards (conversation_id, artwork_id, download_url, created_at) VALUES
(1, 1, 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample1/download', '2025-09-09 10:05:00'),
(1, 2, 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample2/download', '2025-09-09 10:06:00'),
(2, 3, 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample3/download', '2025-09-09 11:05:00'),
(3, 1, 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample4/download', '2025-09-09 12:05:00');

-- ===========================================
-- 4. 뷰 생성 (자주 사용되는 조인 쿼리용)
-- ===========================================

-- 대화별 포토카드 통계 뷰
CREATE VIEW conversation_photocard_stats AS
SELECT 
    c.id as conversation_id,
    c.started_at,
    c.ended_at,
    COUNT(DISTINCT asel.artwork_id) as selected_artworks_count,
    COUNT(DISTINCT p.id) as photocards_created,
    MAX(p.created_at) as last_photocard_created
FROM conversations c
LEFT JOIN artwork_selections asel ON c.id = asel.conversation_id
LEFT JOIN photocards p ON c.id = p.conversation_id
GROUP BY c.id, c.started_at, c.ended_at;

-- 작품별 포토카드 생성 통계 뷰
CREATE VIEW artwork_photocard_stats AS
SELECT 
    p.artwork_id,
    COUNT(p.id) as photocards_created,
    COUNT(DISTINCT p.conversation_id) as unique_conversations,
    MIN(p.created_at) as first_created,
    MAX(p.created_at) as last_created
FROM photocards p
GROUP BY p.artwork_id;

-- ===========================================
-- 5. 유용한 쿼리 예시
-- ===========================================

-- 대화별 포토카드 목록 조회
-- SELECT * FROM conversation_photocard_stats WHERE conversation_id = 1;

-- 특정 대화의 포토카드 목록
-- SELECT p.*, c.started_at 
-- FROM photocards p 
-- JOIN conversations c ON p.conversation_id = c.id 
-- WHERE p.conversation_id = 1;

-- 작품별 포토카드 생성 통계
-- SELECT * FROM artwork_photocard_stats ORDER BY photocards_created DESC;

-- ===========================================
-- 6. 완료 메시지
-- ===========================================

SELECT 'Photocard-Maker 서비스 테이블 정리 및 재생성 완료!' as message;
SELECT '생성된 테이블: conversations, photocards, artwork_selections' as tables_created;
SELECT '생성된 뷰: conversation_photocard_stats, artwork_photocard_stats' as views_created;
