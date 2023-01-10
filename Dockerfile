FROM openjdk:17-alpine
ADD /target/tea-bot-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
