# ArteisLuis Backend - Guía de Despliegue en Render

## 🚀 Instrucciones para desplegar en Render

### Prerrequisitos
- Cuenta en [Render.com](https://render.com)
- Repositorio de Git con el código del backend
- PostgreSQL como base de datos

### Pasos para el despliegue:

#### 1. Configurar el repositorio
Asegúrate de que todos los archivos estén en tu repositorio:
- `render.yaml` - Configuración de infraestructura
- `render-build.sh` - Script de construcción
- `render-start.sh` - Script de inicio
- Archivos de configuración (`application.properties`, `application-prod.properties`)

#### 2. Crear el servicio en Render

**Opción A: Usando render.yaml (Recomendado)**
1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Haz clic en "New" → "Blueprint"
3. Conecta tu repositorio de GitHub
4. Render detectará automáticamente el `render.yaml`
5. Revisa la configuración y haz clic en "Apply"

**Opción B: Configuración manual**
1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Haz clic en "New" → "Web Service"
3. Conecta tu repositorio de GitHub
4. Configura los siguientes campos:

**Configuración del Web Service:**
```
Name: artesluis-backend
Language/Runtime: Docker
Root Directory: artesluis-backend (si tu repo tiene subcarpetas)
Dockerfile Path: Dockerfile (o ./Dockerfile)
Build Command: (dejar vacío - Docker manejará el build)
Start Command: (dejar vacío - Docker manejará el start)
```

**⚠️ Nota importante sobre Java en Render:**
Render no tiene "Java" como opción directa en el dropdown de Language. 
Debes seleccionar **"Docker"** y usar el Dockerfile incluido en el proyecto.

#### 3. Variables de entorno
Configura las siguientes variables en Render:

**Variables obligatorias:**
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=[URL de tu base de datos PostgreSQL]
JAVA_OPTS=-Xmx512m -Xms256m
CORS_ORIGINS=https://tu-frontend.onrender.com,https://*.netlify.app
```

**Variables opcionales:**
```
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB
SHOW_SQL=false
LOG_LEVEL=warn
```

#### 4. Base de datos PostgreSQL
1. En Render, crea una nueva base de datos PostgreSQL:
   - Go to Dashboard → "New" → "PostgreSQL"
   - Name: `artesluis-db`
   - Plan: Free (para desarrollo)
2. Copia la URL de conexión y úsala en la variable `DATABASE_URL`

#### 5. Configuración de archivos estáticos
Para servir archivos subidos, asegúrate de que la carpeta `uploads/` esté configurada correctamente:
- Los archivos se guardarán en `/uploads/` en el contenedor
- La aplicación está configurada para servir archivos desde esta ubicación

### 🔗 Endpoints disponibles

Una vez desplegado, tu API estará disponible en:
```
https://tu-servicio.onrender.com
```

**Endpoints principales:**
- `GET /api/health` - Health check
- `GET /api/test` - Test de funcionamiento
- `GET /api/usuarios` - Lista de usuarios
- `POST /api/usuarios` - Crear usuario
- `GET /api/roles` - Lista de roles
- `POST /api/upload/imagen` - Subir imagen

### 🔍 Verificación del despliegue

1. **Health Check**: Visita `https://tu-servicio.onrender.com/api/health`
2. **Test Endpoint**: Visita `https://tu-servicio.onrender.com/api/test`
3. **Logs**: Ve a tu servicio en Render Dashboard → "Logs" para ver los logs en tiempo real

### 📝 Notas importantes

- **Arranque**: El servicio puede tardar 1-2 minutos en arrancar la primera vez
- **Sleep**: Con el plan gratuito, el servicio se duerme después de 15 minutos de inactividad
- **Límites**: Plan gratuito tiene 750 horas/mes y 512MB RAM
- **Base de datos**: PostgreSQL gratuito tiene límite de 1GB

### 🔧 Troubleshooting

**Problemas comunes:**

1. **Error de construcción**: Verifica que el `mvnw` tenga permisos de ejecución
2. **Error de conexión a BD**: Verifica que la variable `DATABASE_URL` esté configurada correctamente
3. **CORS**: Actualiza `CORS_ORIGINS` con la URL de tu frontend
4. **Memoria insuficiente**: Reduce `JAVA_OPTS` si es necesario

**Para ver logs detallados:**
```bash
# En Render Dashboard → tu servicio → Logs
# O activa logs SQL temporalmente:
SHOW_SQL=true
LOG_LEVEL=debug
```

### 🚀 Próximos pasos

1. Configurar tu frontend para conectar con la nueva URL del backend
2. Configurar dominio personalizado (opcional)
3. Configurar SSL/TLS (automático en Render)
4. Monitorear performance y logs

---

¿Necesitas ayuda? Revisa la [documentación oficial de Render](https://render.com/docs) o contacta al equipo de desarrollo.