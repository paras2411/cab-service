FROM openjdk:15-jdk
COPY ./target/cab-service-0.0.1-SNAPSHOT.jar cab-service.jar
ENTRYPOINT ["java", "-jar", "cab-service.jar"]
