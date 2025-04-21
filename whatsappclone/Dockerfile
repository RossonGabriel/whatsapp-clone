FROM openjdk:17-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} whatsapp-clone.jar

EXPOSE 1234
ENTRYPOINT ["java", "-jar", "whatsapp-clone.jar"]