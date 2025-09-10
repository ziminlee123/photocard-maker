# 실제 이미지로 download_link 테스트하기

## 1. 이미지 생성
```bash
# Python 스크립트로 호작도 이미지 생성
python create-tiger-image.py
```

## 2. 애플리케이션 실행
```bash
# Spring Boot 애플리케이션 실행
./mvnw spring-boot:run
```

## 3. 테스트 API 호출
```bash
# 실제 이미지로 테스트 포토카드 생성
curl -X POST "http://localhost:8083/api/photocards/test-with-image" \
  -H "Content-Type: application/json" \
  -d "\"tiger-artwork.jpg\""
```

## 4. 응답 확인
응답에서 `downloadUrl`을 확인하세요:
```json
{
  "id": 1,
  "downloadUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/abc123/download",
  "createdAt": "2024-01-01T12:00:00"
}
```

## 5. 다운로드 테스트
```bash
# 다운로드 URL로 이미지 다운로드
curl -X GET "http://localhost:8083/api/photocards/abc123/download" --output downloaded-tiger.jpg
```

## 6. 브라우저에서 확인
브라우저에서 다운로드 URL을 직접 열어보세요:
```
http://localhost:8083/api/photocards/abc123/download
```

## 기존 테스트 API와의 차이점
- **기존**: `POST /api/photocards/test` - 더미 이미지 생성
- **새로운**: `POST /api/photocards/test-with-image` - 실제 이미지 파일 사용

## 지원하는 이미지 형식
- JPEG (.jpg, .jpeg)
- PNG (.png)
- 기타 ImageIO에서 지원하는 형식
