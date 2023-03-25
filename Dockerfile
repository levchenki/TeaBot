FROM maven:3.8.1-openjdk-17-slim as MAVEN_BUILD
WORKDIR /app-build
ADD pom.xml .
RUN mvn verify clean --fail-never
ADD . .
RUN mvn clean package

FROM openjdk:17-slim
WORKDIR /app
COPY --from=MAVEN_BUILD /app-build/target/TeaBot-1.0.0-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]