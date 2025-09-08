# Multi-stage build
FROM eclipse-temurin:17-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle wrapper와 build.gradle 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .

# 의존성 다운로드
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew build --no-daemon -x test

# 실행 단계
FROM eclipse-temurin:17-jre

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8081

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
