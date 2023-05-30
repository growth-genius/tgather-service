FROM eclipse-temurin:17-jre-focal
WORKDIR application
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar application.jar"]
