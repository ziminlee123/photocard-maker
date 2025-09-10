-- 테스트용 포토카드 데이터 삽입
INSERT INTO photocards (conversation_id, artwork_id, download_url, created_at) 
VALUES (
    1, 
    1, 
    'https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/test-123/download',
    NOW()
);

-- 생성된 데이터 확인
SELECT * FROM photocards WHERE download_url LIKE '%test-123%';
