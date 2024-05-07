FROM openjdk:17-jdk-slim
LABEL maintainer="test@test.com"
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=./build/libs/testapiserver-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} testapiserver.jar
ENTRYPOINT ["java","-jar","/testapiserver.jar"]