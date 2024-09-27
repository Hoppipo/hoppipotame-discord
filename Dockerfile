FROM openjdk:21
WORKDIR /app
COPY build/libs/*.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]