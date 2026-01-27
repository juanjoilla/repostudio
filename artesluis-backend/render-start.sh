#!/bin/bash
echo "=== Iniciando aplicación ArteisLuis Backend ==="

# Configurar variables de entorno por defecto si no existen
export JAVA_OPTS="${JAVA_OPTS:--Xmx512m -Xms256m}"
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"

echo "Puerto: $PORT"
echo "Perfil de Spring: $SPRING_PROFILES_ACTIVE"
echo "Opciones de Java: $JAVA_OPTS"

# Ejecutar la aplicación
exec java $JAVA_OPTS -Dserver.port=$PORT -jar target/artesluis-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE