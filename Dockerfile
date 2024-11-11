FROM openjdk:21-jdk
WORKDIR /app
COPY build/libs/*-all.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]