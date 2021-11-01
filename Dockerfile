FROM amazoncorretto:11-alpine-jdk

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG JAR_FILE=./api/target/api-*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]