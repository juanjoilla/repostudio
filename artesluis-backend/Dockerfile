# Use the official Eclipse Temurin JDK 17 image (recommended OpenJDK distribution)
FROM eclipse-temurin:17-jdk-alpine

# Install necessary packages
RUN apk add --no-cache bash

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first for better Docker layer caching
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy the source code (this will invalidate cache only when source changes)
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests -q

# Create uploads directory with proper permissions
RUN mkdir -p uploads && chmod 755 uploads

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file with proper memory settings for containers
CMD ["java", \
     "-Djava.awt.headless=true", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-Dserver.port=${PORT:-8080}", \
     "-Xmx400m", \
     "-Xms200m", \
     "-jar", "target/artesluis-0.0.1-SNAPSHOT.jar", \
     "--spring.profiles.active=prod"]