# 서비스명: photocard-maker
역할: 전시회 작품 기반 포토카드 생성 서비스
포트: 8081
---
### 주요 기능
POST `/api/photocards` – 포토카드 생성 (작품 ID 기반)
POST `/api/photocards/upload` – 파일 업로드 포토카드 생성
GET `/api/photocards/{id}` – 포토카드 조회
GET `/api/photocards?artworkId={artworkId}` – 작품별 포토카드 목록 조회
POST `/api/artworks/{artworkId}/select` – 작품 선택 처리
GET `/api/photocards/{fileId}/download` – 포토카드 다운로드
GET `/api/photocards/{fileId}/preview` – 포토카드 미리보기
---
### 실행 방법
````bash
./gradlew bootRun
````
또는
````bash
docker build -t photocard-maker .
docker run -p 8081:8081 photocard-maker
````
### API 테스트
Swagger UI: http://localhost:8081/swagger-ui/index.html
- 포토카드 생성, 조회, 다운로드 등 모든 API 테스트 가능
- OpenAPI 3.0 스펙 기반 문서화
---
### 데이터베이스 정보
**MySQL 8.0 (Azure Database)**
- 호스트: db-guidely-photocard-v0.mysql.database.azure.com:3306
- 데이터베이스: photocarddb
- 사용자: appphotocard
- SSL 연결 필수

**주요 테이블:**
- `photocards`: 포토카드 정보 저장
- `artwork_selections`: 작품 선택 정보 저장

**DDL 스크립트:**
```sql
CREATE TABLE photocards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artwork_id BIGINT NOT NULL,
    download_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE artwork_selections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artwork_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
---
### 외부 서비스 연동
**Exhibition 서비스**: https://guidely-exhibition-artwork-services-dmeagqebfud4e7hh.koreacentral-01.azurewebsites.net
- 작품 정보 조회 API 연동
- 작품 이미지 및 메타데이터 제공

**Azure Blob Storage**
- 포토카드 이미지 저장
- 공개 URL 생성 및 제공
---
### 기술 스택
- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: MySQL 8.0 (Azure)
- **Storage**: Azure Blob Storage
- **Build**: Gradle 8.5
- **Documentation**: Swagger/OpenAPI 3
- **Container**: Docker
---
### 담당자
**김지민** - 포토카드 생성 로직, 이미지 처리, Azure Storage 연동
- 주요 기능: 작품 기반 포토카드 자동 생성, 이미지 리사이징 및 텍스트 오버레이
- 기술 담당: Spring Boot, Azure Services, Image Processing
---