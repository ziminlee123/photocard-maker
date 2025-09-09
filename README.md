# 📸 Photocard Maker

전시회 작품을 기반으로 개인화된 포토카드를 생성하는 Spring Boot 애플리케이션입니다.

## 🎯 주요 기능

- **작품 선택**: 사용자가 관람한 작품 중 선택
- **포토카드 생성**: 선택한 작품과 감성 문구를 조합한 포토카드 자동 생성
- **이미지 처리**: 작품 이미지 다운로드, 리사이즈, 텍스트 오버레이
- **파일 관리**: 생성된 포토카드 이미지 저장 및 URL 제공
- **외부 API 연동**: Exhibition 서비스(작품 정보), Chat-Orchestra 서비스(엔딩크레딧)

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Photocard-     │    │  Exhibition-    │    │  Chat-Orchestra │
│  Maker Service  │◄───┤  Service        │    │  Service        │
│                 │    │                 │    │                 │
│ • 포토카드 생성   │    │ • 작품 정보     │    │ • 엔딩크레딧     │
│ • 이미지 처리    │    │ • 작품 이미지   │    │ • 대화 요약     │
│ • 파일 관리      │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │
         ▼
┌─────────────────┐
│  MySQL 8.0      │
│  (Azure DB)     │
│                 │
│ • 포토카드 데이터 │
│ • 메타데이터     │
└─────────────────┘
```

## 🔧 기술 스택

- **Backend**: Spring Boot 3.2.0
- **Database**: MySQL 8.0 (Azure Database)
- **Build Tool**: Gradle 8.5
- **Java**: OpenJDK 17
- **Image Processing**: Java AWT, ImageIO
- **API Documentation**: Swagger/OpenAPI 3
- **Containerization**: Docker
- **Cloud**: Azure App Service

## 📁 프로젝트 구조

```
src/main/java/com/photocard/
├── controller/
│   └── PhotocardController.java          # 포토카드 REST API
├── service/
│   ├── PhotocardService.java             # 포토카드 비즈니스 로직
│   ├── ExternalApiService.java           # 외부 API 연동
│   ├── MetadataCombinationService.java   # 메타데이터 조합
│   ├── PhotocardFileService.java         # 파일 관리
│   └── ImageProcessingService.java       # 이미지 처리
├── entity/
│   └── Photocard.java                    # 포토카드 엔티티
├── repository/
│   └── PhotocardRepository.java          # JPA 리포지토리
├── dto/
│   ├── PhotocardCreateRequest.java       # 포토카드 생성 요청
│   ├── PhotocardResponse.java            # 포토카드 응답
│   ├── ExternalArtworkResponse.java      # 외부 작품 응답
│   └── EndingCreditResponse.java         # 엔딩크레딧 응답
└── config/
    ├── OpenApiConfig.java                # Swagger 설정
    └── ExternalApiConfig.java            # 외부 API 설정
```

## ⚙️ 설정

### application.yml

```yaml
spring:
  application:
    name: photocard-maker
  
  datasource:
    url: jdbc:mysql://${DB_HOST:db-guidely-photocard-v0.mysql.database.azure.com}:3306/${DB_NAME:photocard_db}?useSSL=true&serverTimezone=UTC
    username: ${DB_USERNAME:userapp}
    password: ${DB_PASSWORD:your_password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true

  web:
    cors:
      allowed-origins: "*"
      allowed-methods: "*"
      allowed-headers: "*"

file:
  upload-dir: ${FILE_UPLOAD_DIR:/tmp/photocards}
  base-url: ${FILE_BASE_URL:https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net}

external:
  exhibition:
    base-url: ${EXHIBITION_API_URL:http://localhost:8082}
  chat-orchestra:
    base-url: ${CHAT_ORCHESTRA_API_URL:http://localhost:8080}

logging:
  level:
    com.photocard: DEBUG
    org.springframework.web: DEBUG
```

## 🚀 실행 방법

### 1. 로컬 실행

```bash
# 의존성 설치
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 2. Docker 실행

```bash
# Docker 이미지 빌드
docker build -t photocard-maker .

# Docker 컨테이너 실행
docker run -p 8081:8081 \
  -e DB_HOST=your-db-host \
  -e DB_USERNAME=your-username \
  -e DB_PASSWORD=your-password \
  photocard-maker
```

### 3. Azure 배포

```bash
# Docker Hub에 푸시
docker tag photocard-maker ziminlee123/photocard-maker:latest
docker push ziminlee123/photocard-maker:latest

# Azure App Service에서 Docker 이미지 배포
# Azure Portal에서 Container Settings 설정
```

## 📡 API 엔드포인트

### 포토카드 관리

#### 1. 포토카드 생성

**URL:** `POST https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "artworkId": 1,
  "sessionId": "test-session-123",
  "title": "아름다운 작품 포토카드",
  "description": "전시회에서 본 멋진 작품을 포토카드로 만들어봤습니다."
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "artworkId": 1,
  "sessionId": "test-session-123",
  "title": "아름다운 작품 포토카드",
  "description": "전시회에서 본 멋진 작품을 포토카드로 만들어봤습니다.",
  "previewUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/file123/preview",
  "downloadUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/file123/download",
  "status": "COMPLETED",
  "endingCreditId": "credit-001",
  "conversationSummary": "사용자가 작품에 대해 대화한 내용 요약",
  "artworkMetadata": "{\"artist\":\"작가명\",\"year\":2024}",
  "endingCreditMetadata": "{\"participants\":[\"사용자1\",\"사용자2\"]}",
  "combinedMetadata": "{\"totalDuration\":300,\"artworkType\":\"painting\"}",
  "createdAt": "2025-09-09T00:38:26",
  "updatedAt": "2025-09-09T00:38:27"
}
```

#### 2. 포토카드 조회

**URL:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/{id}`

**예시:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/1`

**Response (200 OK):** 위와 동일한 포토카드 정보

#### 3. 세션별 포토카드 목록 조회

**URL:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards?sessionId={sessionId}`

**예시:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards?sessionId=test-session-123`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "artworkId": 1,
    "sessionId": "test-session-123",
    "title": "아름다운 작품 포토카드",
    "description": "전시회에서 본 멋진 작품을 포토카드로 만들어봤습니다.",
    "previewUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/file123/preview",
    "downloadUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/file123/download",
    "status": "COMPLETED",
    "createdAt": "2025-09-09T00:38:26"
  }
]
```

#### 4. 작품 선택 (Chat-Orchestra에서 호출)

**URL:** `POST https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/conversation/{sessionId}/artworks/{artworkId}/select`

**예시:** `POST https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/conversation/test-session-123/artworks/1/select`

**Response (200 OK):** 포토카드 생성 결과

#### 5. 포토카드 다운로드

**URL:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/{fileId}/download`

**예시:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/file123/download`

**Response:** 파일 바이너리 데이터 (JPEG 이미지)

#### 6. 포토카드 미리보기

**URL:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/{fileId}/preview`

**예시:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/file123/preview`

**Response:** 이미지 파일 (JPEG)

#### 7. 테스트 포토카드 생성

**URL:** `POST https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/test`

**Response (201 Created):** 테스트용 포토카드 생성

## 📊 데이터베이스 스키마

### photocards 테이블

```sql
CREATE TABLE photocards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artwork_id BIGINT NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    description TEXT,
    ending_credit_id BIGINT,
    conversation_summary TEXT,
    artwork_metadata TEXT,
    ending_credit_metadata TEXT,
    combined_metadata TEXT,
    preview_url VARCHAR(500),
    download_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'GENERATING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_artwork_id (artwork_id)
);
```

## 🔄 서비스 간 통신

### Exhibition 서비스 호출
```java
// 작품 정보 조회
GET http://localhost:8082/api/artworks/{artworkId}
```

### Chat-Orchestra 서비스 호출
```java
// 엔딩크레딧 조회
GET http://localhost:8080/api/ending-credits/{endingCreditId}
```

## 🧪 테스트 방법

### 1. Swagger UI 사용 (권장)

**URL**: https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/swagger-ui/index.html

* 브라우저에서 위 URL에 접속
* 각 API 엔드포인트를 클릭하여 "Try it out" 버튼 사용
* Request Body를 입력하고 "Execute" 버튼 클릭

### 2. Postman 사용

1. **포토카드 생성 테스트**
   - Method: POST
   - URL: `https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards`
   - Headers: `Content-Type: application/json`
   - Body: 위의 Request Body 예시 사용

2. **포토카드 조회 테스트**
   - Method: GET
   - URL: `https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/1`

### 3. curl 사용

```bash
# 포토카드 생성
curl -X POST "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards" \
  -H "Content-Type: application/json" \
  -d '{
    "artworkId": 1,
    "sessionId": "test-session-123",
    "title": "테스트 포토카드",
    "description": "테스트용 포토카드입니다"
  }'

# 포토카드 조회
curl -X GET "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/1"
```

### 4. PowerShell 사용 (Windows)

```powershell
# 포토카드 생성
Invoke-RestMethod -Uri "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{
    "artworkId": 1,
    "sessionId": "test-session-123",
    "title": "테스트 포토카드",
    "description": "테스트용 포토카드입니다"
  }'

# 포토카드 조회
Invoke-RestMethod -Uri "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/1" `
  -Method GET
```

## 🚨 에러 처리

### 일반적인 HTTP 상태 코드

- **200 OK**: 요청 성공
- **201 Created**: 리소스 생성 성공
- **400 Bad Request**: 잘못된 요청 (필수 필드 누락, 잘못된 데이터 타입)
- **404 Not Found**: 리소스를 찾을 수 없음
- **500 Internal Server Error**: 서버 내부 오류

### 포토카드 생성 관련 에러

- **이미지 라이선스 없음**: `이미지 라이선스가 없어 포토카드를 생성할 수 없습니다.`
- **외부 API 호출 실패**: `외부 서비스 호출에 실패했습니다.`
- **이미지 처리 실패**: `포토카드 이미지 생성에 실패했습니다.`

## 📚 API 문서

- **Swagger UI**: https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/swagger-ui/index.html
- **OpenAPI Spec**: https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/v3/api-docs

## 🔧 개발 환경 설정

### 필수 요구사항

- Java 17 이상
- Gradle 8.5 이상
- MySQL 8.0 이상
- Docker (선택사항)

### 환경 변수

```bash
# 데이터베이스 설정
export DB_HOST=your-db-host
export DB_USERNAME=your-username
export DB_PASSWORD=your-password
export DB_NAME=photocard_db

# 파일 저장소 설정
export FILE_UPLOAD_DIR=/tmp/photocards
export FILE_BASE_URL=https://your-domain.com

# 외부 API 설정
export EXHIBITION_API_URL=http://localhost:8082
export CHAT_ORCHESTRA_API_URL=http://localhost:8080
```

## 📝 TODO

### ✅ 완료된 기능
- [x] 기본 포토카드 생성 API
- [x] 작품 정보 연동 (Exhibition 서비스)
- [x] 엔딩크레딧 연동 (Chat-Orchestra 서비스)
- [x] 이미지 처리 및 렌더링
- [x] 파일 저장 및 URL 생성
- [x] Swagger API 문서화
- [x] Docker 컨테이너화
- [x] Azure 배포

### 🔄 진행 중인 기능
- [ ] 성능 최적화
- [ ] 에러 처리 개선

### 📋 향후 계획
- [ ] 인기 작품 통계 서비스 연동
- [ ] 더 많은 템플릿 옵션
- [ ] 사용자 인증 및 권한 관리
- [ ] 포토카드 공유 기능
- [ ] 모바일 앱 연동

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 연락처

프로젝트 링크: [https://github.com/ziminlee123/photocard-maker](https://github.com/ziminlee123/photocard-maker)

---

**Photocard Maker** - 전시회 작품을 포토카드로 만들어보세요! 📸✨