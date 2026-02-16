@echo off
cd /d "%~dp0"
echo ========================================
echo   Iniciando Artes Luis - Modo Desarrollo (H2)
echo ========================================
echo.
echo Current directory: %CD%
echo.
echo Configurando perfil: dev
set SPRING_PROFILES_ACTIVE=dev
echo.
echo Ejecutando Maven Spring Boot...
echo.
mvn spring-boot:run
pause
