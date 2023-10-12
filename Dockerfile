FROM maven:latest AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM openjdk:18
WORKDIR /app
COPY --from=build /app/target/LinkShortener-1.0.0-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "LinkShortener-1.0.0-SNAPSHOT.jar"]