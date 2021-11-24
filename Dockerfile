FROM openjdk:11.0.13-jdk-slim
VOLUME /tmp
ADD *.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/app.jar" ]