# jar 파일 빌드
FROM eclipse-temurin:11 as builder

WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Maven 종속성 미리 다운로드하여 캐싱 최적화
RUN ./mvnw dependency:resolve && ./mvnw dependency:go-offline

COPY ./src ./src

# -Dmaven.test.skip=true 이 mvn install 단계에 설정되지 않으면
# 환경변수가 주입되지 않은 상태에서 어플리케이션이 실행되어 에러 발생
RUN ./mvnw clean install -Dmaven.test.skip=true

# jar 실행
FROM eclipse-temurin:11 as runtime

# Google Chrome 설치
RUN apt-get update && \
    apt-get install -y wget gnupg curl ca-certificates && \
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" | tee /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

RUN addgroup --system --gid 1000 worker
RUN adduser --system --uid 1000 --ingroup worker --disabled-password worker
USER worker:worker

WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "/opt/app/app.jar"]
