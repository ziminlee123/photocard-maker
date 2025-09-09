# 📸 Photocard-Maker Service

MSA 팀 프로젝트의 포토카드 생성 서비스입니다. 사용자가 선택한 작품을 기반으로 포토카드를 생성하고 관리합니다.

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
│ Search Index &  │    │   H2 Database   │    │   External      │
│   VectorDB      │    │  (In-Memory)    │    │ License API     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 주요 기능

- **작품 선택**: 사용자가 대화 중 선택한 작품을 기반으로 포토카드 생성
- **외부 API 연동**: Exhibition 서비스에서 작품 메타데이터 조회
- **라이선스 확인**: 이미지 사용 가능 여부 확인
- **포토카드 관리**: 생성, 조회, 세션별 관리
- **URL 생성**: 미리보기 및 다운로드 URL 자동 생성

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
│   │   │   └── PhotocardController.java      # REST API 컨트롤러 (Swagger 문서화)
│   │   ├── dto/
│   │   │   ├── PhotocardCreateRequest.java   # 포토카드 생성 요청 DTO
│   │   │   ├── PhotocardResponse.java        # 포토카드 응답 DTO
│   │   │   ├── ExternalArtworkResponse.java  # 외부 작품 정보 DTO
│   │   │   └── EndingCreditResponse.java     # 엔딩크레딧 응답 DTO
│   │   ├── entity/
│   │   │   └── Photocard.java                # 포토카드 엔티티 (JPA)
│   │   ├── repository/
│   │   │   └── PhotocardRepository.java      # 포토카드 리포지토리 (JPA)
│   │   └── service/
│   │       ├── PhotocardService.java         # 포토카드 비즈니스 로직
│   │       └── ExternalApiService.java       # 외부 API 호출 서비스
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

## 📡 API 엔드포인트

### 1. 포토카드 생성
```http
POST /api/photocards
Content-Type: application/json

{
  "artworkId": 1,
  "sessionId": "test-session-001",
  "title": "테스트 포토카드",
  "description": "테스트용 포토카드입니다"
}
```

**응답:**
```json
{
  "id": 1,
  "artworkId": 1,
  "sessionId": "test-session-001",
  "title": "테스트 포토카드",
  "description": "테스트용 포토카드입니다",
  "previewUrl": "https://photocard-maker.com/preview/uuid",
  "downloadUrl": "https://photocard-maker.com/download/uuid",
  "status": "GENERATING",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### 2. 포토카드 조회
```http
GET /api/photocards/{id}
```

### 3. 세션별 포토카드 목록 조회
```http
GET /api/photocards?sessionId={sessionId}
```

### 4. 작품 선택 (Chat-Orchestra에서 호출)
```http
POST /api/conversation/{sessionId}/artworks/{artworkId}/select
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

### 1. Postman 사용
- Postman Collection을 생성하여 위의 API들을 테스트
- 환경 변수 설정: `baseUrl = http://localhost:8081`

### 2. curl 명령어
```bash
# 헬스체크
curl -X GET http://localhost:8081/actuator/health

# 포토카드 생성
curl -X POST http://localhost:8081/api/photocards \
  -H "Content-Type: application/json" \
  -d '{
    "artworkId": 1,
    "sessionId": "test-session-001",
    "title": "테스트 포토카드"
  }'
```

### 3. PowerShell (Windows)
```powershell
# 헬스체크
Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method GET

# 포토카드 생성
$body = @{
    artworkId = 1
    sessionId = "test-session-001"
    title = "테스트 포토카드"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/photocards" -Method POST -Body $body -ContentType "application/json"
```

## 🔧 환경변수 설정

### 필수 환경변수
```bash
# 데이터베이스 설정
SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/photocarddb?serverTimezone=UTC&sslMode=REQUIRED&useUnicode=true&characterEncoding=utf8
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password

# 외부 API 설정
EXHIBITION_API_URL=http://localhost:8082
CHAT_ORCHESTRA_API_URL=http://localhost:8080

# 서버 포트 (선택사항)
WEBSITES_PORT=8081

# JPA 설정 (선택사항)
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Azure 환경변수 설정
Azure App Service에서 환경변수를 설정할 때는 다음 값들을 사용하세요:
- `SPRING_DATASOURCE_URL`: Azure MySQL 연결 문자열
- `SPRING_DATASOURCE_USERNAME`: Azure MySQL 사용자명
- `SPRING_DATASOURCE_PASSWORD`: Azure MySQL 비밀번호

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
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_artwork_id (artwork_id)
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
- **404**: 포토카드를 찾을 수 없음
- **500**: 외부 서비스 연동 실패, 데이터베이스 연결 실패
- **400**: 잘못된 요청 데이터, 유효성 검사 실패
- **503**: 외부 API 서비스 사용 불가

## 📚 API 문서

### Swagger UI
- **URL**: http://localhost:8081/swagger-ui.html
- **기능**: 인터랙티브 API 문서 및 테스트 도구
- **지원**: 모든 REST API 엔드포인트 자동 문서화

### OpenAPI 3.0 스펙
- **JSON**: http://localhost:8081/v3/api-docs
- **YAML**: http://localhost:8081/v3/api-docs.yaml
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

## 📝 TODO

- [x] MySQL 데이터베이스 연동
- [x] Swagger/OpenAPI 문서화
- [x] Docker 컨테이너화
- [x] 환경변수 기반 설정
- [ ] 실제 이미지 생성 라이브러리 연동
- [ ] 비동기 포토카드 렌더링 구현 (Redis 큐 사용)
- [ ] 외부 라이선스 API 실제 연동
- [ ] 포토카드 캐싱 구현 (Redis)
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 작성
- [ ] CI/CD 파이프라인 구축
- [ ] 모니터링 및 로깅 강화

## 👥 팀원

- **김대희**: Photocard-Maker 서비스 개발

## 📄 라이선스

이 프로젝트는 MSA 팀 프로젝트의 일부입니다.

---

**문의사항이 있으시면 이슈를 등록해주세요!** 🚀
