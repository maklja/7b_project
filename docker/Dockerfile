FROM amazoncorretto:11-alpine-jdk

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

#ARG JAR_FILE=../api/target/*.jar
#COPY ${JAR_FILE} app.jar
COPY api/target/api-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]