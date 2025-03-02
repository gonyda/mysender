# Step 1: Build Stage
FROM eclipse-temurin:11 as builder

WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Maven 종속성 다운로드 최적화
RUN ./mvnw verify --fail-never

COPY ./src ./src

# 테스트를 건너뛰고 빌드 수행
RUN ./mvnw clean install -Dmaven.test.skip=true

# Step 2: Runtime Stage
FROM eclipse-temurin:11 as runtime

# Google Chrome 설치
RUN apt-get update && \
    apt-get install -y wget gnupg curl ca-certificates && \
    curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome-keyring.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" | tee /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# 권한 설정
RUN addgroup --system --gid 1000 worker
RUN adduser --system --uid 1000 --ingroup worker --disabled-password worker
USER worker:worker

WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/app.jar
RUN chown worker:worker /opt/app/app.jar

# JVM 최적화 옵션 추가
ENV PROFILE=default
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "/opt/app/app.jar"]