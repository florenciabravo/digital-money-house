FROM eclipse-temurin:17-jre
ARG JAR_FILE=target/user.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080