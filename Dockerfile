FROM gradle:7.6-jdk17 as builder
WORKDIR /build
# 그래들 파일이 변경되었을 때만 새롭게 의존패키지 다운로드 받게함.
COPY build.gradle ../settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true
# 빌더 이미지에서 애플리케이션 빌드
COPY . /build
RUN gradle build -x test --parallel
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

RUN apt-get -y update

RUN apt -y install wget

RUN apt -y install unzip

RUN apt -y install curl

RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb

RUN apt -y install ./google-chrome-stable_current_amd64.deb

RUN wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/` curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE`/chromedriver_linux64.zip

RUN unzip /tmp/chromedriver.zip chromedriver -d /usr/bin
#RUN apk add curl
COPY --from=builder /build/build/libs/*.jar ./app.jar
ENV	USE_PROFILE dev

ENTRYPOINT ["java", "-Dspring.profiles.active=${USE_PROFILE}", "-jar", "/app/app.jar"]