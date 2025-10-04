# --- Stage 1: Build ---
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY ./pom.xml pom.xml
RUN mvn dependency:go-offline -B

COPY ./src src/
RUN mvn clean package -Dmaven.test.skip=true


# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8761
CMD ["java", "-jar", "app.jar"]
