./mvnw package && java -jar api/target/api-1.0-SNAPSHOT.jar

docker build -f ./docker/Dockerfile  -t 7b/project . 