-- artwork_selections 테이블의 외래키 제약 조건 제거
ALTER TABLE artwork_selections DROP FOREIGN KEY artwork_selections_ibfk_1;

-- conversation_id 컬럼 제거 (필요한 경우)
-- ALTER TABLE artwork_selections DROP COLUMN conversation_id;

-- 또는 conversation_id 컬럼을 nullable로 변경
ALTER TABLE artwork_selections MODIFY COLUMN conversation_id BIGINT NULL;
