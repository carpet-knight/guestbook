FROM openjdk:11

EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=5s CMD curl http://localhost:8080/health

ARG JAR_FILE
WORKDIR /app
COPY ${JAR_FILE} /app/app.jar

ENV SPRING_PROFILES_ACTIVE=memory,local,file

CMD ["java", "-jar", "/app/app.jar"]
