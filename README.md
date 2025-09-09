# 📸 Photocard-Maker Service

MSA 팀 프로젝트의 포토카드 생성 서비스입니다. 사용자가 선택한 작품을 기반으로 실제 이미지가 포함된 포토카드를 생성하고 관리합니다.

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Chat-Orchestra │    │ Photocard-Maker │    │   Exhibition    │
│     Service     │───▶│     Service     │───▶│    Service      │
│   (포트: 8080)  │    │   (포트: 8081)  │    │   (포트: 8082)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Search Index &  │    │   MySQL 8.0     │    │   External      │
│   VectorDB      │    │ (Azure Database)│    │ License API     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 주요 기능

- **실제 이미지 생성**: 작품 이미지를 다운로드하여 포토카드 템플릿에 적용
- **메타데이터 통합**: 작품 정보와 엔딩크레딧 정보를 결합한 포토카드 생성
- **템플릿 시스템**: 다양한 포토카드 템플릿 지원 (기본, 클래식, 모던 등)
- **파일 관리**: 로컬 파일 시스템 기반 이미지 저장 및 URL 생성
- **외부 API 연동**: Exhibition 서비스에서 작품 메타데이터 조회
- **엔딩크레딧 연동**: Chat-Orchestra 서비스에서 대화 요약 정보 조회
- **포토카드 관리**: 생성, 조회, 세션별 관리

## 📁 프로젝트 구조

```
photocard-maker/
├── build.gradle                          # Gradle 빌드 설정
├── gradlew.bat                           # Gradle Wrapper (Windows)
├── Dockerfile                            # Docker 이미지 설정
├── docker-compose.yml                    # Docker Compose 설정
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar            # Gradle Wrapper JAR
│       └── gradle-wrapper.properties     # Gradle Wrapper 설정
├── src/main/
│   ├── java/com/photocard/
│   │   ├── PhotocardMakerApplication.java    # 메인 애플리케이션
│   │   ├── config/
│   │   │   ├── ExternalApiConfig.java        # 외부 API 설정
│   │   │   └── OpenApiConfig.java            # Swagger/OpenAPI 설정
│   │   ├── controller/
│   │   │   ├── PhotocardController.java      # 포토카드 REST API 컨트롤러
│   │   │   ├── FileController.java           # 파일 관리 REST API 컨트롤러
│   │   │   └── PhotocardTemplateController.java # 템플릿 관리 REST API 컨트롤러
│   │   ├── dto/
│   │   │   ├── PhotocardCreateRequest.java   # 포토카드 생성 요청 DTO
│   │   │   ├── PhotocardResponse.java        # 포토카드 응답 DTO
│   │   │   ├── ExternalArtworkResponse.java  # 외부 작품 정보 DTO
│   │   │   └── EndingCreditResponse.java     # 엔딩크레딧 응답 DTO
│   │   ├── entity/
│   │   │   ├── Photocard.java                # 포토카드 엔티티 (JPA)
│   │   │   └── PhotocardTemplate.java        # 포토카드 템플릿 엔티티 (JPA)
│   │   ├── repository/
│   │   │   ├── PhotocardRepository.java      # 포토카드 리포지토리 (JPA)
│   │   │   └── PhotocardTemplateRepository.java # 템플릿 리포지토리 (JPA)
│   │   └── service/
│   │       ├── PhotocardService.java         # 포토카드 비즈니스 로직
│   │       ├── ExternalApiService.java       # 외부 API 호출 서비스
│   │       ├── MetadataCombinationService.java # 메타데이터 통합 서비스
│   │       ├── FileStorageService.java       # 파일 저장 관리 서비스
│   │       ├── ImageProcessingService.java   # 이미지 처리 서비스
│   │       └── PhotocardTemplateService.java # 템플릿 관리 서비스
│   └── resources/
│       └── application.yml                   # 애플리케이션 설정 (환경변수 지원)
└── README.md
```

## 🔧 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **MySQL 8.0** (Azure Database)
- **Gradle 8.5**
- **Lombok**
- **Spring Web**
- **Spring Validation**
- **Spring Boot Actuator**
- **Swagger/OpenAPI 3** (SpringDoc)
- **Java AWT** (이미지 처리)
- **ImageIO** (이미지 입출력)
- **Docker**

## ⚙️ 설정

### application.yml
```yaml
server:
  port: ${WEBSITES_PORT:8081}
  tomcat:
    connection-timeout: 20000
    max-connections: 8192
    accept-count: 100
    max-threads: 200

spring:
  application:
    name: photocard-maker
  
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://db-guidely-photocard-v0.mysql.database.azure.com:3306/photocarddb?serverTimezone=UTC&sslMode=REQUIRED&useUnicode=true&characterEncoding=utf8}
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${SPRING_DATASOURCE_USERNAME:userapp}
    password: ${SPRING_DATASOURCE_PASSWORD:userpw}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
  
  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true

# External API endpoints
external:
  exhibition:
    base-url: ${EXHIBITION_API_URL:http://localhost:8082}
  chat-orchestra:
    base-url: ${CHAT_ORCHESTRA_API_URL:http://localhost:8080}

# File storage configuration
file:
  upload-dir: ${FILE_UPLOAD_DIR:./uploads}
  base-url: ${FILE_BASE_URL:http://localhost:8081}

logging:
  level:
    com.photocard: DEBUG
    org.springframework.web: DEBUG
```

## 🚀 실행 방법

### 1. 프로젝트 클론
```bash
git clone https://github.com/kimdaehee-sian/mas_back_Photocard-Maker.git
cd mas_back_Photocard-Maker
```

### 2. 애플리케이션 실행

#### 로컬 실행
```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

#### Docker 실행
```bash
# Docker 이미지 빌드
docker build -t photocard-maker .

# Docker 컨테이너 실행
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://your-mysql-host:3306/photocarddb" \
  -e SPRING_DATASOURCE_USERNAME="your-username" \
  -e SPRING_DATASOURCE_PASSWORD="your-password" \
  photocard-maker

# Docker Compose 실행
docker-compose up -d
```

### 3. 서비스 확인
- **애플리케이션**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API 문서**: http://localhost:8081/v3/api-docs
- **헬스체크**: http://localhost:8081/actuator/health

## 🔧 환경변수 설정

### 필수 환경변수
```bash
# 데이터베이스 설정
SPRING_DATASOURCE_URL=jdbc:mysql://db-guidely-photocard-v0.mysql.database.azure.com:3306/photocarddb?serverTimezone=UTC&sslMode=REQUIRED&useUnicode=true&characterEncoding=utf8
SPRING_DATASOURCE_USERNAME=userapp
SPRING_DATASOURCE_PASSWORD=userpw

# 외부 API 설정
EXHIBITION_API_URL=http://localhost:8082
CHAT_ORCHESTRA_API_URL=http://localhost:8080

# 서버 포트 (선택사항)
WEBSITES_PORT=8081

# JPA 설정 (선택사항)
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# 파일 저장소 설정
FILE_UPLOAD_DIR=./uploads
FILE_BASE_URL=http://localhost:8081
```

### Azure 환경변수 설정
Azure App Service에서 환경변수를 설정할 때는 다음 값들을 사용하세요:
- `SPRING_DATASOURCE_URL`: Azure MySQL 연결 문자열
- `SPRING_DATASOURCE_USERNAME`: Azure MySQL 사용자명
- `SPRING_DATASOURCE_PASSWORD`: Azure MySQL 비밀번호

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
  "previewUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/files/preview/23369aad-a432-4b48-a255-6575ce4f6943",
  "downloadUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/files/download/abc7683f-cc45-4149-b273-0eb3a4472386",
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

**Response (200 OK):**
```json
{
  "id": 1,
  "artworkId": 1,
  "sessionId": "test-session-001",
  "title": "아름다운 작품 포토카드",
  "description": "전시회에서 본 멋진 작품을 포토카드로 만들어봤습니다.",
  "previewUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/files/preview/23369aad-a432-4b48-a255-6575ce4f6943",
  "downloadUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/files/download/abc7683f-cc45-4149-b273-0eb3a4472386",
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
    "previewUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/files/preview/23369aad-a432-4b48-a255-6575ce4f6943",
    "downloadUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/files/download/abc7683f-cc45-4149-b273-0eb3a4472386",
    "status": "COMPLETED",
    "endingCreditId": "credit-001",
    "conversationSummary": "사용자가 작품에 대해 대화한 내용 요약",
    "artworkMetadata": "{\"artist\":\"작가명\",\"year\":2024}",
    "endingCreditMetadata": "{\"participants\":[\"사용자1\",\"사용자2\"]}",
    "combinedMetadata": "{\"totalDuration\":300,\"artworkType\":\"painting\"}",
    "createdAt": "2025-09-09T00:38:26",
    "updatedAt": "2025-09-09T00:38:27"
  }
]
```

#### 4. 테스트 데이터 생성

**URL:** `POST https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/test`

**Headers:** 없음 (자동으로 테스트 데이터 생성)

**Response (201 Created):**
```json
{
  "id": 2,
  "artworkId": 1,
  "sessionId": "test-session-1725851234567",
  "title": "테스트 포토카드",
  "description": "개발용 테스트 포토카드입니다",
  "previewUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/files/preview/test-uuid",
  "downloadUrl": "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/files/download/test-uuid",
  "status": "COMPLETED",
  "endingCreditId": "credit-001",
  "conversationSummary": "테스트 대화 요약",
  "artworkMetadata": "{\"artist\":\"테스트 작가\",\"year\":2024}",
  "endingCreditMetadata": "{\"participants\":[\"테스트 사용자\"]}",
  "combinedMetadata": "{\"totalDuration\":300,\"artworkType\":\"painting\"}",
  "createdAt": "2025-09-09T05:12:40",
  "updatedAt": "2025-09-09T05:12:40"
}
```

### 파일 관리

#### 5. 파일 미리보기
```http
GET /api/files/preview/{fileId}
```

#### 6. 파일 다운로드
```http
GET /api/files/download/{fileId}
```

#### 7. 파일 업로드
```http
POST /api/files/upload
Content-Type: multipart/form-data
```

#### 8. 파일 삭제
```http
DELETE /api/files/{fileId}
```

### 템플릿 관리

#### 9. 모든 템플릿 조회

**URL:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "기본 템플릿",
    "description": "기본 포토카드 템플릿입니다",
    "templateImageUrl": "https://example.com/template.jpg",
    "width": 800,
    "height": 600,
    "type": "CLASSIC",
    "isActive": true,
    "layoutConfig": "{\"textAreas\":[{\"x\":50,\"y\":50,\"width\":700,\"height\":100,\"fontSize\":24,\"color\":\"#000000\"}],\"imageAreas\":[{\"x\":100,\"y\":200,\"width\":600,\"height\":300}]}",
    "createdAt": "2025-09-09T05:03:19",
    "updatedAt": "2025-09-09T05:03:19"
  }
]
```

#### 10. 타입별 템플릿 조회

**URL:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates/type/{type}`

**예시:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates/type/CLASSIC`

**사용 가능한 타입:** `CLASSIC`, `MODERN`, `MINIMAL`, `ARTISTIC`, `CUSTOM`

#### 11. 특정 템플릿 조회

**URL:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates/{id}`

**예시:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates/1`

#### 12. 기본 템플릿 조회

**URL:** `GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates/default`

#### 13. 템플릿 생성

**URL:** `POST https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "기본 템플릿",
  "description": "기본 포토카드 템플릿입니다",
  "templateImageUrl": "https://example.com/template.jpg",
  "width": 800,
  "height": 600,
  "type": "CLASSIC",
  "isActive": true,
  "layoutConfig": "{\"textAreas\":[{\"x\":50,\"y\":50,\"width\":700,\"height\":100,\"fontSize\":24,\"color\":\"#000000\"}],\"imageAreas\":[{\"x\":100,\"y\":200,\"width\":600,\"height\":300}]}"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "기본 템플릿",
  "description": "기본 포토카드 템플릿입니다",
  "templateImageUrl": "https://example.com/template.jpg",
  "width": 800,
  "height": 600,
  "type": "CLASSIC",
  "isActive": true,
  "layoutConfig": "{\"textAreas\":[{\"x\":50,\"y\":50,\"width\":700,\"height\":100,\"fontSize\":24,\"color\":\"#000000\"}],\"imageAreas\":[{\"x\":100,\"y\":200,\"width\":600,\"height\":300}]}",
  "createdAt": "2025-09-09T05:03:19",
  "updatedAt": "2025-09-09T05:03:19"
}
```

#### 14. 템플릿 수정

**URL:** `PUT https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates/{id}`

**Headers:**
```
Content-Type: application/json
```

**Request Body:** (수정할 필드만 포함)
```json
{
  "name": "수정된 템플릿",
  "description": "수정된 설명",
  "isActive": false
}
```

#### 15. 템플릿 삭제

**URL:** `DELETE https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates/{id}`

**예시:** `DELETE https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates/1`

**Response (200 OK):** 빈 응답

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
* Request body를 입력하고 "Execute" 버튼 클릭
* 실제 응답을 바로 확인 가능

### 2. Postman 사용
- Postman Collection을 생성하여 위의 API들을 테스트
- 환경 변수 설정: `baseUrl = https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net`

### 3. curl 명령어
```bash
# 헬스체크
curl -X GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/actuator/health

# 포토카드 생성
curl -X POST https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards \
  -H "Content-Type: application/json" \
  -d '{
    "artworkId": 1,
    "sessionId": "test-session-123",
    "title": "테스트 포토카드",
    "description": "curl로 생성한 테스트 포토카드입니다"
  }'

# 템플릿 조회
curl -X GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/templates

# 포토카드 조회
curl -X GET https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards/1
```

### 4. PowerShell (Windows)
```powershell
# 헬스체크
Invoke-RestMethod -Uri "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/actuator/health" -Method GET

# 포토카드 생성
$body = @{
    artworkId = 1
    sessionId = "test-session-123"
    title = "PowerShell 테스트 포토카드"
    description = "PowerShell로 생성한 테스트 포토카드입니다"
} | ConvertTo-Json

Invoke-RestMethod -Uri "https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/api/photocards" -Method POST -Body $body -ContentType "application/json"
```

### 5. 빠른 테스트 순서

1. **헬스체크**: `GET /actuator/health`
2. **템플릿 조회**: `GET /api/templates`
3. **포토카드 생성**: `POST /api/photocards`
4. **포토카드 조회**: `GET /api/photocards/{id}`
5. **세션별 조회**: `GET /api/photocards?sessionId={sessionId}`

## 📊 데이터베이스 스키마

### Photocard 테이블 (MySQL)
```sql
CREATE TABLE photocards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artwork_id BIGINT NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    preview_url VARCHAR(500) NOT NULL,
    download_url VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    ending_credit_id VARCHAR(255),
    conversation_summary TEXT,
    artwork_metadata TEXT,
    ending_credit_metadata TEXT,
    combined_metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_artwork_id (artwork_id)
);
```

### PhotocardTemplate 테이블 (MySQL)
```sql
CREATE TABLE photocard_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    template_image_url VARCHAR(500),
    width INT NOT NULL,
    height INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    layout_config TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_is_active (is_active)
);
```

## 🔍 포토카드 상태

- **GENERATING**: 포토카드 생성 중
- **COMPLETED**: 포토카드 생성 완료
- **FAILED**: 포토카드 생성 실패

## 🚨 에러 처리

### 일반적인 에러 응답
```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "작품 정보를 가져올 수 없습니다",
  "path": "/api/photocards"
}
```

### 주요 에러 케이스
- **404**: 포토카드를 찾을 수 없음, 파일을 찾을 수 없음, 템플릿을 찾을 수 없음
- **500**: 외부 서비스 연동 실패, 데이터베이스 연결 실패, 이미지 처리 실패
- **400**: 잘못된 요청 데이터, 유효성 검사 실패, 지원하지 않는 파일 형식
- **503**: 외부 API 서비스 사용 불가, 파일 저장소 접근 불가

## 📚 API 문서

### Swagger UI
- **URL**: https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/swagger-ui/index.html
- **기능**: 인터랙티브 API 문서 및 테스트 도구
- **지원**: 모든 REST API 엔드포인트 자동 문서화

### OpenAPI 3.0 스펙
- **JSON**: https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/v3/api-docs
- **YAML**: https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/v3/api-docs.yaml
- **기능**: API 스펙을 JSON/YAML 형태로 제공

## 🔧 개발 환경 설정

### IDE 설정
- **IntelliJ IDEA** 또는 **Eclipse** 권장
- **Lombok** 플러그인 설치 필요
- **Java 17** SDK 설정

### 디버깅
- MySQL 데이터베이스에서 데이터 상태 확인
- 로그 레벨을 DEBUG로 설정하여 상세 로그 확인
- Swagger UI에서 API 테스트 및 디버깅
- Spring Boot Actuator를 통한 애플리케이션 상태 모니터링
- 파일 시스템에서 생성된 이미지 파일 확인

## 📝 TODO

### ✅ 완료된 기능
- [x] MySQL 데이터베이스 연동
- [x] Swagger/OpenAPI 문서화
- [x] Docker 컨테이너화
- [x] 환경변수 기반 설정
- [x] 메타데이터 통합 시스템 (Phase 1)
- [x] 파일 저장 및 관리 시스템 (Phase 1)
- [x] 실제 이미지 생성 및 처리 (Phase 2)
- [x] 포토카드 템플릿 시스템 (Phase 2)
- [x] 엔딩크레딧 연동 (Phase 1)

### 🚧 진행 예정
- [ ] 비동기 포토카드 렌더링 구현 (Redis 큐 사용)
- [ ] 외부 라이선스 API 실제 연동
- [ ] 포토카드 캐싱 구현 (Redis)
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 작성
- [ ] CI/CD 파이프라인 구축
- [ ] 모니터링 및 로깅 강화
- [ ] 이미지 최적화 및 압축
- [ ] 다양한 포토카드 템플릿 추가


## 📄 라이선스

이 프로젝트는 MSA 팀 프로젝트의 일부입니다.

---

**문의사항이 있으시면 이슈를 등록해주세요!** 🚀
