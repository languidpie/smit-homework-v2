# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the fat JAR
RUN ./gradlew shadowJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:21-jre

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
