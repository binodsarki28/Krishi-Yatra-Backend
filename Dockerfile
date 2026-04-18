# ==============================
# 1. BUILD STAGE
# ==============================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Step 1: Copy only the Gradle configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Step 2: Make wrapper executable and download dependencies.
# Because the source code isn't copied yet, Docker will cache this layer!
# This means your build won't re-download dependencies unless build.gradle changes.
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# Step 3: Copy the actual source code and build the application
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# ==============================
# 2. RUN STAGE
# ==============================
# Using 'jre' instead of 'jdk' and using 'alpine' for a drastically smaller and more secure final image.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
