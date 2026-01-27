#!/usr/bin/env bash
# Build script for Render deployment

echo "Starting build process..."

# Clean and build the project
./mvnw clean package -DskipTests

echo "Build completed successfully!"