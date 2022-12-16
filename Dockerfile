FROM openjdk:18
EXPOSE 8084
WORKDIR /app
COPY target/LinkShortener-0.0.1-SNAPSHOT.jar .
ENTRYPOINT [ "java", "-jar", "LinkShortener-0.0.1-SNAPSHOT.jar" ]