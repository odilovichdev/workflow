FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon
COPY src/ ./src/
RUN ./gradlew clean bootJar -x test --no-daemon

# Runtime stage: use minimal runtime container and copy the .jar file
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
