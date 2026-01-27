#!/bin/bash
echo "=== Iniciando proceso de construcción para Render ==="

# Limpiar y construir el proyecto
echo "Limpiando y construyendo el proyecto..."
./mvnw clean package -DskipTests

# Verificar que el JAR se creó correctamente
if [ -f "target/artesluis-backend-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ JAR creado exitosamente"
    ls -la target/*.jar
else
    echo "❌ Error: No se pudo crear el JAR"
    exit 1
fi

echo "=== Construcción completada ===">