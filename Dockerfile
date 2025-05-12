# FROM eclipse-temurin:21-jdk-jammy AS builder
# WORKDIR /app
# COPY gradle/ gradle/
# COPY gradlew build.gradle settings.gradle ./
# RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon
# COPY src/ ./src/
# RUN ./gradlew clean build --no-daemon

# FROM eclipse-temurin:21-jre-jammy
# WORKDIR /app
# COPY build/libs/bxbatuz-0.0.1-SNAPSHOT.jar bxbatuz.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "bxbatuz.jar"]

# Builder stage: build .jar file
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon
COPY src/ ./src/
RUN ./gradlew clean bootJar -x test --no-daemon

# Runtime stage: use minimal runtime container and copy the .jar file
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
