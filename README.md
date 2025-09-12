# Photocard-Maker Service

## 서비스명: photocard-maker
**역할**: 포토카드 생성 및 관리 서비스  
**포트**: 8081  
**기술 스택**: Spring Boot 3.2.0, Java 17, MySQL, Azure Storage

---

## 주요 기능

### 포토카드 관리
- `POST /api/photocards` – 포토카드 생성
- `GET /api/photocards/{id}` – 포토카드 조회
- `GET /api/photocards?artworkId={artworkId}` – 작품별 포토카드 목록 조회
- `GET /api/photocards/{fileId}/download` – 포토카드 다운로드
- `GET /api/photocards/{fileId}/preview` – 포토카드 미리보기

### 작품 선택
- `POST /api/artworks/{artworkId}/select` – 작품 선택 (Chat-Orchestra에서 호출)

---

## 실행 방법

### 로컬 실행
```bash
./gradlew bootRun
```

### Docker 실행
```bash
# Docker 이미지 빌드
docker build -t photocard-maker .

# Docker 컨테이너 실행
docker run -p 8081:8081 photocard-maker
```

### Docker Compose 실행
```bash
docker-compose up -d
```

---

## API 테스트

### Swagger UI
- **로컬**: http://localhost:8081/swagger-ui.html
- **프로덕션**: https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net/swagger-ui.html

### API 문서
- **OpenAPI 3.0**: http://localhost:8081/api-docs

---

## 데이터베이스 정보

### 데이터베이스: MySQL
- **드라이버**: com.mysql.cj.jdbc.Driver
- **방언**: org.hibernate.dialect.MySQL8Dialect

### 주요 테이블 구조

#### photocards
```sql
CREATE TABLE photocards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artwork_id BIGINT NOT NULL,
    download_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### artwork_selections
```sql
CREATE TABLE artwork_selections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artwork_id BIGINT NOT NULL,
    selected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### DDL 스크립트
- **전체 스키마**: `database_schema.sql`
- **최소 스키마**: `photocard_minimal_schema.sql`
- **백업 스크립트**: `photocarddb_backup_20250911_184341.sql`

---

## 환경 설정

### 필수 환경 변수
```bash
# 데이터베이스 설정
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/photocarddb
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# Azure Storage 설정
AZURE_STORAGE_CONNECTION_STRING=your_connection_string
AZURE_STORAGE_CONTAINER_NAME=photocards
AZURE_STORAGE_BASE_URL=your_base_url

# 외부 API 설정
EXHIBITION_API_URL=http://localhost:8082
CHAT_ORCHESTRA_API_URL=http://localhost:8080
```

### .env 파일 지원
프로젝트는 `.env` 파일을 통한 환경 변수 설정을 지원합니다.

---

## 아키텍처

### MSA 구조
- **Chat-Orchestra Service** (포트: 8080) - 대화 관리
- **Exhibition Service** (포트: 8082) - 전시회 및 작품 관리
- **Photocard-Maker Service** (포트: 8081) - 포토카드 생성 및 관리

### 주요 컴포넌트
- **PhotocardController**: REST API 엔드포인트
- **PhotocardService**: 비즈니스 로직
- **AzureStorageService**: Azure Blob Storage 연동
- **ExternalApiService**: 외부 서비스 API 호출
- **ImageProcessingService**: 이미지 처리


## 배포 정보

### Azure 배포
- **프로덕션 URL**: https://guidely-phtotcardmaker-g9hqaacaadcwdhfn.koreacentral-01.azurewebsites.net
- **Docker 이미지**: ziminlee123/photocard-maker:latest

### Docker 설정
- **Multi-stage build** 사용으로 이미지 크기 최적화
- **Health check** 설정으로 컨테이너 상태 모니터링
- **포트**: 8081

---

## 개발 정보

### 의존성 관리
- **Gradle**: 빌드 도구
- **Spring Boot**: 3.2.0
- **Java**: 17
- **Lombok**: 코드 간소화
- **SpringDoc OpenAPI**: API 문서화

### 주요 라이브러리
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'com.azure:azure-storage-blob:12.21.1'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
}
```

---

## 담당자

### 개발자: 이지민
**담당 기능**:
- 포토카드 생성 및 관리 API
- Azure Storage 연동
- 이미지 처리 서비스
- Docker 컨테이너화 및 배포

=======
### 담당자
**이지민** - 포토카드 생성 로직, 이미지 처리, Azure Storage 연동
- 주요 기능: 작품 기반 포토카드 자동 생성, 이미지 리사이징 및 텍스트 오버레이
- 기술 담당: Spring Boot, Azure Services, Image Processing

