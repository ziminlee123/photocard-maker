-- ===========================================
-- 포토카드 메이커 서비스 데이터베이스 스키마
-- ERD 기반 테이블 생성 스크립트
-- ===========================================

-- 1. Chat-Orchestra Service Tables
-- ===========================================

CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    INDEX idx_started_at (started_at)
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    speaker VARCHAR(20) NOT NULL CHECK (speaker IN ('user', 'assistant')),
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS ending_credits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id)
);

-- 2. Exhibition & Artwork Service Tables
-- ===========================================

CREATE TABLE IF NOT EXISTS exhibitions (
    id VARCHAR(36) PRIMARY KEY,  -- UUID as VARCHAR(36)
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date)
);

CREATE TABLE IF NOT EXISTS artworks (
    id VARCHAR(36) PRIMARY KEY,  -- UUID as VARCHAR(36)
    exhibition_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255),
    era VARCHAR(100),
    description TEXT,
    image_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exhibition_id) REFERENCES exhibitions(id) ON DELETE CASCADE,
    INDEX idx_exhibition_id (exhibition_id),
    INDEX idx_artist (artist),
    INDEX idx_era (era)
);

-- 3. Photocard-Maker Service Tables
-- ===========================================

CREATE TABLE IF NOT EXISTS photocards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    artwork_id VARCHAR(36) NOT NULL,  -- UUID as VARCHAR(36)
    download_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (artwork_id) REFERENCES artworks(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_artwork_id (artwork_id),
    INDEX idx_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS artwork_selections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    artwork_id VARCHAR(36) NOT NULL,  -- UUID as VARCHAR(36)
    selected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (artwork_id) REFERENCES artworks(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_artwork_id (artwork_id),
    INDEX idx_selected_at (selected_at)
);

-- 4. RAG Service Tables
-- ===========================================

CREATE TABLE IF NOT EXISTS rag_responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    response TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    INDEX idx_message_id (message_id),
    INDEX idx_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS rag_sources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rag_response_id BIGINT NOT NULL,
    source_url TEXT NOT NULL,
    snippet TEXT,
    rank INT NOT NULL DEFAULT 1,
    FOREIGN KEY (rag_response_id) REFERENCES rag_responses(id) ON DELETE CASCADE,
    INDEX idx_rag_response_id (rag_response_id),
    INDEX idx_rank (rank)
);

-- 5. 통계 서비스 Tables
-- ===========================================

-- 작품 통계 (대희 추가)
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artwork_id VARCHAR(36) NOT NULL,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (artwork_id) REFERENCES artworks(id) ON DELETE CASCADE,
    UNIQUE KEY unique_artwork_like (artwork_id),
    INDEX idx_like_count (like_count)
);

-- 단어 빈도 통계 (예락 추가)
CREATE TABLE IF NOT EXISTS word_frequency (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(255) NOT NULL,
    ending_credit_id BIGINT NOT NULL,
    conversation_id BIGINT NOT NULL,
    frequency INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ending_credit_id) REFERENCES ending_credits(id) ON DELETE CASCADE,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_word (word),
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_frequency (frequency DESC)
);

-- ===========================================
-- 샘플 데이터 삽입 (테스트용)
-- ===========================================

-- 샘플 전시회 데이터
INSERT INTO exhibitions (id, title, description, start_date, end_date) VALUES
('550e8400-e29b-41d4-a716-446655440001', '르네상스의 거장들', '15-16세기 르네상스 시대의 대표적인 작가들의 작품을 전시합니다.', '2025-01-01', '2025-12-31'),
('550e8400-e29b-41d4-a716-446655440002', '인상주의의 혁신', '19세기 인상주의 화가들의 혁신적인 작품들을 만나보세요.', '2025-02-01', '2025-11-30');

-- 샘플 작품 데이터
INSERT INTO artworks (id, exhibition_id, title, artist, era, description, image_url) VALUES
('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', '모나리자', '레오나르도 다 빈치', '르네상스', '세계에서 가장 유명한 미소의 여인', 'https://example.com/mona-lisa.jpg'),
('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', '최후의 만찬', '레오나르도 다 빈치', '르네상스', '예수의 마지막 식사 장면', 'https://example.com/last-supper.jpg'),
('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', '해돋이', '클로드 모네', '인상주의', '아침 햇살이 비치는 인상적인 풍경', 'https://example.com/impression-sunrise.jpg');

-- 샘플 대화 데이터
INSERT INTO conversations (id, started_at) VALUES
(1, '2025-09-09 10:00:00'),
(2, '2025-09-09 11:00:00');

-- 샘플 메시지 데이터
INSERT INTO messages (conversation_id, speaker, content, created_at) VALUES
(1, 'user', '안녕하세요! 미술에 대해 궁금한 것이 있어요.', '2025-09-09 10:00:00'),
(1, 'assistant', '안녕하세요! 미술에 대해 무엇이 궁금하신가요?', '2025-09-09 10:00:30'),
(1, 'user', '르네상스 시대의 대표적인 작가가 누구인가요?', '2025-09-09 10:01:00'),
(1, 'assistant', '르네상스 시대의 대표적인 작가로는 레오나르도 다 빈치, 미켈란젤로, 라파엘로 등이 있습니다.', '2025-09-09 10:01:30');

-- 샘플 엔딩크레딧 데이터
INSERT INTO ending_credits (conversation_id, content, created_at) VALUES
(1, '이 대화는 AI가 도움을 드렸습니다. 감사합니다!', '2025-09-09 10:05:00'),
(2, '미술의 세계로 함께 떠난 시간이었습니다.', '2025-09-09 11:05:00');

-- 샘플 작품 선택 데이터
INSERT INTO artwork_selections (conversation_id, artwork_id, selected_at) VALUES
(1, '660e8400-e29b-41d4-a716-446655440001', '2025-09-09 10:03:00'),
(1, '660e8400-e29b-41d4-a716-446655440002', '2025-09-09 10:04:00');

-- 샘플 포토카드 데이터
INSERT INTO photocards (conversation_id, artwork_id, download_url, created_at) VALUES
(1, '660e8400-e29b-41d4-a716-446655440001', 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample1/download', '2025-09-09 10:05:00'),
(1, '660e8400-e29b-41d4-a716-446655440002', 'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/sample2/download', '2025-09-09 10:06:00');

-- 샘플 좋아요 데이터
INSERT INTO likes (artwork_id, like_count) VALUES
('660e8400-e29b-41d4-a716-446655440001', 150),
('660e8400-e29b-41d4-a716-446655440002', 89),
('660e8400-e29b-41d4-a716-446655440003', 203);

-- 샘플 단어 빈도 데이터
INSERT INTO word_frequency (word, ending_credit_id, conversation_id, frequency) VALUES
('미술', 1, 1, 3),
('르네상스', 1, 1, 2),
('작가', 1, 1, 2),
('대화', 1, 1, 1),
('AI', 1, 1, 1);

-- ===========================================
-- 인덱스 최적화
-- ===========================================

-- 자주 사용되는 조인 쿼리를 위한 복합 인덱스
CREATE INDEX idx_photocards_conversation_artwork ON photocards(conversation_id, artwork_id);
CREATE INDEX idx_artwork_selections_conversation_artwork ON artwork_selections(conversation_id, artwork_id);
CREATE INDEX idx_messages_conversation_created ON messages(conversation_id, created_at);

-- ===========================================
-- 뷰 생성 (자주 사용되는 조인 쿼리용)
-- ===========================================

-- 포토카드 상세 정보 뷰
CREATE VIEW photocard_details AS
SELECT 
    p.id,
    p.conversation_id,
    p.artwork_id,
    p.download_url,
    p.created_at,
    a.title as artwork_title,
    a.artist,
    a.era,
    a.image_url,
    e.title as exhibition_title
FROM photocards p
JOIN artworks a ON p.artwork_id = a.id
JOIN exhibitions e ON a.exhibition_id = e.id;

-- 대화별 작품 선택 통계 뷰
CREATE VIEW conversation_artwork_stats AS
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

SELECT 'Database schema created successfully!' as message;
