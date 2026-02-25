# Guía de Testing con Postman - Spring Security

Esta guía te muestra cómo probar todos los endpoints de autenticación y autorización usando Postman.

## ⚙️ Configuración del Entorno

### Diferencia entre Desarrollo y Producción

**🏠 Desarrollo (Local):**
- URL Base: `http://localhost:8080`
- Correr aplicación localmente con `mvnw spring-boot:run` o `run-dev.bat`
- Base de datos: Local o Render (según application-dev.properties)

**☁️ Producción (Render):**
- URL Base: `https://tu-servicio.onrender.com` (tu URL de Render)
- Aplicación desplegada en Render
- Puerto interno: 10000 (Render lo maneja automáticamente)
- **⚠️ IMPORTANTE:** No uses `localhost` ni agregues `:10000` a la URL
- Usa la URL pública completa que te proporciona Render

### Variables de Entorno en Postman (RECOMENDADO)

1. Ve a **Environments** en Postman
2. Crea dos entornos:

**Entorno "Local":**
```
baseUrl: http://localhost:8080
```

**Entorno "Render (Producción)":**
```
baseUrl: https://tu-servicio.onrender.com
```
*(Reemplaza `tu-servicio` con el nombre real de tu servicio en Render)*

3. En todos los requests, usa `{{baseUrl}}` en lugar de la URL completa
4. Cambia de entorno según necesites en el dropdown superior derecho

## 📋 Configuración Inicial de Postman

### 1. Crear una Nueva Colección
- Nombre: "Artes Luis API"
- Crea carpetas: "Autenticación", "Planes", "Admin", "Usuarios", "Órdenes"

### 2. Habilitar Cookies (MUY IMPORTANTE)
Postman necesita manejar cookies para mantener la sesión:

**Opción A - Interceptor (Recomendado):**
1. Ve a Settings (⚙️) → General
2. Activa "Interceptor" o "Postman Agent"
3. Esto permite que Postman maneje cookies automáticamente

**Opción B - Cookie Manager:**
1. Ve al ícono de 🍪 (Cookies) debajo de "Send"
2. Agrega el dominio: `localhost:8080`
3. Las cookies se guardarán automáticamente

## 🔐 Sección 1: Autenticación

### 1.1 Login con Form Data

**Endpoint:** `POST {{baseUrl}}/login`

**Ejemplo Local:** `POST http://localhost:8080/login`  
**Ejemplo Render:** `POST https://tu-servicio.onrender.com/login`

**Headers:**
```
Content-Type: application/x-www-form-urlencoded
```

**Body (x-www-form-urlencoded):**
```
correo: admin@artesluis.com
password: admin123
```

**Respuesta Esperada:**
- Status: `302 Found` (redirección a `/admin`)
- Headers incluirán `Set-Cookie: JSESSIONID=...`
- Postman guardará automáticamente esta cookie

**Usuarios de Prueba:**
```
# Admin
correo: admin@artesluis.com
password: admin123

# Cliente
correo: juan.cliente@email.com
password: cliente123

# Artista
correo: maria.gonzalez@email.com
password: artista123

# Moderador
correo: monica.mod@artesluis.com
password: mod123
```

### 1.2 Login API (Alternativa JSON)

**Endpoint:** `POST {{baseUrl}}/api/usuarios/login`

**Ejemplo Local:** `POST http://localhost:8080/api/usuarios/login`  
**Ejemplo Render:** `POST https://tu-servicio.onrender.com/api/usuarios/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "correo": "admin@artesluis.com",
  "password": "admin123"
}
```

**Respuesta Esperada:**
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "usuario": {
    "id": 1,
    "nombre": "Luis Artista",
    "correo": "admin@artesluis.com",
    "imagenUrl": null,
    "rol": "ADMIN"
  }
}
```

### 1.3 Verificar Sesión Activa

Después del login, todas las peticiones subsiguientes deben incluir la cookie `JSESSIONID` automáticamente.

**Test:** `GET {{baseUrl}}/api/admin/usuarios/auto`

**Ejemplo Local:** `GET http://localhost:8080/api/admin/usuarios/auto`  
**Ejemplo Render:** `GET https://tu-servicio.onrender.com/api/admin/usuarios/auto`

**Headers:**
```
Cookie: JSESSIONID=<valor-obtenido-del-login>
```
*(Postman lo hace automáticamente si está configurado correctamente)*

**Respuesta Esperada:** Lista de usuarios (solo si eres ADMIN o MODERADOR)

### 1.4 Logout

**Endpoint:** `POST {{baseUrl}}/logout`

**Ejemplo:** `POST https://tu-servicio.onrender.com/logout`

**Headers:**
```
Cookie: JSESSIONID=<tu-sesion>
```

**Respuesta:** Redirección a `/login?logout=true`

---

## 📂 Sección 2: Endpoints Públicos (Sin Autenticación)

### 2.1 Listar Planes

**Endpoint:** `GET {{baseUrl}}/api/planes`

**Ejemplo:** `GET https://tu-servicio.onrender.com/api/planes`

**Headers:** Ninguno necesario

**Respuesta Esperada:**
```json
[
  {
    "id": 1,
    "nombre": "Plan Básico",
    "precio": 150.00,
    "descripcion": "...",
    "esRecomendado": false,
    "estaActivo": true
  },
  ...
]
```

### 2.2 Registro de Usuario

**Endpoint:** `POST {{baseUrl}}/api/usuarios/registro`

**Ejemplo:** `POST https://tu-servicio.onrender.com/api/usuarios/registro`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "nombre": "Nuevo Usuario",
  "correo": "nuevo@ejemplo.com",
  "password": "password123",
  "rol": {
    "id": 2
  }
}
```
*(Nota: id 2 es típicamente "CLIENTE")*

**Respuesta Esperada:**
```json
{
  "success": true,
  "message": "Usuario creado exitosamente",
  "usuario": {
    "id": 10,
    "nombre": "Nuevo Usuario",
    "correo": "nuevo@ejemplo.com",
    "rol": "CLIENTE"
  }
}
```

### 2.3 Estadísticas Públicas

**Endpoint:** `GET {{baseUrl}}/api/data/stats`

**Ejemplo:** `GET https://tu-servicio.onrender.com/api/data/stats`

**Respuesta Esperada:**
```json
{
  "totalUsuarios": 12,
  "totalRoles": 4
}
```

---

## 🔒 Sección 3: Endpoints Protegidos - Requieren Autenticación

### 3.1 Obtener Perfil de Usuario

**Prerequisito:** Estar logueado

**Endpoint:** `GET {{baseUrl}}/api/usuarios/perfil/{id}`

**Ejemplo:** `GET {{baseUrl}}/api/usuarios/perfil/1`  
**Ejemplo completo:** `GET https://tu-servicio.onrender.com/api/usuarios/perfil/1`

**Headers:**
```
Cookie: JSESSIONID=<tu-sesion>
```

**Respuesta Esperada:**
```json
{
  "success": true,
  "usuario": {
    "id": 1,
    "nombre": "Luis Artista",
    "correo": "admin@artesluis.com",
    "imagenUrl": "",
    "rol": "ADMIN"
  }
}
```

### 3.2 Actualizar Perfil

**Endpoint:** `PUT {{baseUrl}}/api/usuarios/perfil/{id}`

**Ejemplo:** `PUT https://tu-servicio.onrender.com/api/usuarios/perfil/1`

**Headers:**
```
Content-Type: application/json
Cookie: JSESSIONID=<tu-sesion>
```

**Body:**
```json
{
  "nombre": "Luis Artista - Actualizado",
  "imagenUrl": "https://example.com/nueva-imagen.jpg"
}
```

### 3.3 Cambiar Contraseña

**Endpoint:** `PUT {{baseUrl}}/api/usuarios/cambiar-password/{id}`

**Ejemplo:** `PUT https://tu-servicio.onrender.com/api/usuarios/cambiar-password/1`

**Headers:**
```
Content-Type: application/json
Cookie: JSESSIONID=<tu-sesion>
```

**Body:**
```json
{
  "passwordActual": "admin123",
  "passwordNueva": "nuevaPassword123"
}
```

---

## 👑 Sección 4: Endpoints Solo ADMIN

### 4.1 Listar Todos los Usuarios

**Prerequisito:** Estar logueado como ADMIN

**Endpoint:** `GET {{baseUrl}}/api/admin/usuarios`

**Ejemplo:** `GET https://tu-servicio.onrender.com/api/admin/usuarios`

**Headers:**
```
Cookie: JSESSIONID=<tu-sesion-admin>
```

**Respuesta Esperada:**
```json
{
  "success": true,
  "usuarios": [
    {
      "id": 1,
      "nombre": "Luis Artista",
      "correo": "admin@artesluis.com",
      "imagenUrl": "",
      "rol": "ADMIN"
    },
    ...
  ],
  "total": 12
}
```

**Error si no eres ADMIN:**
```
Status: 403 Forbidden
{
  "timestamp": "2026-02-23T...",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

### 4.2 Crear Nuevo Plan

**Endpoint:** `POST {{baseUrl}}/api/planes`

**Ejemplo:** `POST https://tu-servicio.onrender.com/api/planes`

**Headers:**
```
Content-Type: application/json
Cookie: JSESSIONID=<tu-sesion-admin>
```

**Body:**
```json
{
  "nombre": "Plan Premium Plus",
  "precio": 350.00,
  "descripcion": "Plan con todas las características",
  "numeroRevisiones": 5,
  "archivosIncluidos": "PSD, AI, PNG, JPG",
  "caracteristicas": "- Diseño personalizado\n- 5 revisiones\n- Archivos fuente",
  "esRecomendado": true,
  "estaActivo": true,
  "colorBadge": "#FFD700"
}
```

**Respuesta Esperada:**
```json
{
  "id": 4,
  "nombre": "Plan Premium Plus",
  "precio": 350.00,
  ...
}
```

### 4.3 Actualizar Plan

**Endpoint:** `PUT {{baseUrl}}/api/planes/{id}`

**Ejemplo:** `PUT https://tu-servicio.onrender.com/api/planes/1`

**Headers:**
```
Content-Type: application/json
Cookie: JSESSIONID=<tu-sesion-admin>
```

**Body:** (misma estructura que crear)

### 4.4 Eliminar Plan

**Endpoint:** `DELETE {{baseUrl}}/api/planes/{id}`

**Ejemplo:** `DELETE https://tu-servicio.onrender.com/api/planes/1`

**Headers:**
```
Cookie: JSESSIONID=<tu-sesion-admin>
```

**Respuesta Esperada:**
```
Status: 204 No Content
```

### 4.5 Listar Órdenes

**Endpoint:** `POST {{baseUrl}}/api/admin/ordenes`

**Ejemplo:** `POST https://tu-servicio.onrender.com/api/admin/ordenes`

**Headers:**
```
Content-Type: application/json
Cookie: JSESSIONID=<tu-sesion-admin>
```

**Body:**
```json
{
  "numeroOrden": "",
  "estado": "",
  "emailCliente": "",
  "page": 0,
  "size": 20
}
```

**Respuesta Esperada:**
```json
{
  "success": true,
  "ordenes": [...],
  "currentPage": 0,
  "totalPages": 1,
  "totalItems": 5
}
```

### 4.6 Obtener Detalle de Orden

**Endpoint:** `GET {{baseUrl}}/api/admin/ordenes/{id}`

**Ejemplo:** `GET https://tu-servicio.onrender.com/api/admin/ordenes/1`

**Headers:**
```
Cookie: JSESSIONID=<tu-sesion-admin>
```

### 4.7 Actualizar Estado de Orden

**Endpoint:** `PUT {{baseUrl}}/api/admin/ordenes/{id}/estado`

**Ejemplo:** `PUT https://tu-servicio.onrender.com/api/admin/ordenes/1/estado`

**Headers:**
```
Content-Type: application/json
Cookie: JSESSIONID=<tu-sesion-admin>
```

**Body:**
```json
{
  "nuevoEstado": "COMPLETADO"
}
```

Estados válidos: `PENDIENTE`, `PAGADO`, `EN_PROCESO`, `COMPLETADO`, `CANCELADO`

### 4.8 Estadísticas de Ventas

**Endpoint:** `POST {{baseUrl}}/api/admin/ordenes/estadisticas`

**Ejemplo:** `POST https://tu-servicio.onrender.com/api/admin/ordenes/estadisticas`

**Headers:**
```
Content-Type: application/json
Cookie: JSESSIONID=<tu-sesion-admin>
```

**Body:**
```json
{
  "dias": 30
}
```

### 4.9 Gestionar Roles

**Endpoint:** `GET {{baseUrl}}/api/roles`

**Ejemplo:** `GET https://tu-servicio.onrender.com/api/roles`

**Headers:**
```
Cookie: JSESSIONID=<tu-sesion-admin>
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "ADMIN"
  },
  {
    "id": 2,
    "nombre": "CLIENTE"
  },
  ...
]
```

---

## 🎨 Sección 5: Endpoints ADMIN o ARTISTA

### 5.1 Subir Imagen

**Endpoint:** `POST {{baseUrl}}/api/upload/imagen`

**Ejemplo:** `POST https://tu-servicio.onrender.com/api/upload/imagen`

**Headers:**
```
Cookie: JSESSIONID=<tu-sesion>
```

**Body (form-data):**
```
Key: file
Type: File
Value: [Seleccionar archivo]
```

**Respuesta Esperada:**
```
/uploads/imagen-nombre.jpg
```

---

## 🔄 Sección 6: Testing de Autorizaciones

### Test 1: Usuario no autenticado intenta acceder a recurso protegido

**Request:** `GET {{baseUrl}}/api/admin/usuarios`

**Ejemplo:** `GET https://tu-servicio.onrender.com/api/admin/usuarios`

**Sin Cookie de sesión**

**Respuesta Esperada:**
```
Status: 401 Unauthorized
o
Status: 302 Found (redirección a /login)
```

### Test 2: Usuario CLIENTE intenta acceder a recurso de ADMIN

**Steps:**
1. Login como cliente: `juan.cliente@email.com / cliente123`
2. Intentar: `GET {{baseUrl}}/api/admin/usuarios`

**Ejemplo completo:** `GET https://tu-servicio.onrender.com/api/admin/usuarios`

**Respuesta Esperada:**
```
Status: 403 Forbidden
{
  "timestamp": "2026-02-23T...",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

### Test 3: Usuario ARTISTA puede subir archivos

**Steps:**
1. Login como artista: `maria.gonzalez@email.com / artista123`
2. Intentar: `POST {{baseUrl}}/api/upload/imagen`

**Ejemplo:** `POST https://tu-servicio.onrender.com/api/upload/imagen`

**Respuesta Esperada:** `200 OK` con URL del archivo

### Test 4: Usuario ARTISTA NO puede gestionar planes

**Steps:**
1. Login como artista: `maria.gonzalez@email.com / artista123`
2. Intentar: `POST {{baseUrl}}/api/planes`

**Ejemplo:** `POST https://tu-servicio.onrender.com/api/planes`

**Respuesta Esperada:** `403 Forbidden`

---

## 💡 Tips para Postman

### 1. Variables de Entorno
Crea un Environment con:
```
baseUrl: https://tu-servicio.onrender.com
userId: 1
```

Usa así: `{{baseUrl}}/api/admin/usuarios`

### 2. Tests Automáticos
Agrega en la pestaña "Tests":

```javascript
// Guardar JSESSIONID automáticamente
if (pm.cookies.has('JSESSIONID')) {
    pm.environment.set('sessionId', pm.cookies.get('JSESSIONID'));
}

// Verificar respuesta exitosa
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Verificar estructura JSON
pm.test("Response has success field", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('success');
});
```

### 3. Pre-request Script para Login Automático
Si la sesión expiró, puedes agregar:

```javascript
// En Pre-request Script
if (!pm.cookies.has('JSESSIONID')) {
    pm.sendRequest({
        url: pm.environment.get('baseUrl') + '/login',
        method: 'POST',
        header: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: {
            mode: 'urlencoded',
            urlencoded: [
                {key: 'correo', value: 'admin@artesluis.com'},
                {key: 'password', value: 'admin123'}
            ]
        }
    });
}
```

### 4. Colección Exportable
Puedes exportar tu colección y compartirla con el equipo:
1. Click derecho en la colección
2. "Export"
3. Formato Postman Collection v2.1

---

## 🐛 Troubleshooting

### ☁️ IMPORTANTE: Aplicación desplegada en Render

**Si tu aplicación está en Render (no localhost):**

#### ¿Cómo obtener tu URL pública de Render?
1. Ve a tu [Dashboard de Render](https://dashboard.render.com/)
2. Haz clic en tu servicio `artesluis-backend`
3. En la parte superior verás tu URL pública: `https://artesluis-backend-XXXX.onrender.com`
4. **USA ESA URL** en Postman, NO uses `localhost` ni agregues el puerto `:10000`

#### Logs que ves son NORMALES:
```
Tomcat started on port 10000 (http) with context path '/'
Started ArtesluisBackendApplication in 149.5 seconds
```
✅ **Esto significa que tu app está funcionando CORRECTAMENTE**

- Puerto 10000: Es el puerto INTERNO de Render (no necesitas usarlo)
- URL correcta: `https://tu-servicio.onrender.com/api/planes`
- URL INCORRECTA: ❌ `http://localhost:10000/api/planes`
- URL INCORRECTA: ❌ `http://localhost:8080/api/planes`

#### Ejemplo de configuración en Postman:
**Variable de entorno:**
```
baseUrl: https://artesluis-backend-xxxx.onrender.com
```

**Request:**
```
GET {{baseUrl}}/api/planes
POST {{baseUrl}}/login
```

#### Si no puedes acceder a los endpoints:
1. ✅ Verifica que la URL sea la pública de Render (https://...)
2. ✅ NO uses localhost si la app está en Render
3. ✅ NO agregues puerto (:10000 o :8080) a la URL de Render
4. ✅ Prueba primero con un endpoint público: `GET {{baseUrl}}/api/planes`
5. ✅ Verifica en Render Dashboard que el servicio esté "Live" (verde)

---

### Problema: "401 Unauthorized" en todos los endpoints

**Solución:**
1. Verifica que Postman está guardando cookies
2. Haz login primero
3. Verifica que la cookie `JSESSIONID` está presente en las requests subsiguientes
4. Ve a Cookies (🍪) y verifica que existe para `localhost:8080`

### Problema: "403 Forbidden" 

**Causa:** Tu usuario no tiene el rol adecuado

**Solución:**
1. Verifica tu rol: `GET /api/usuarios/perfil/{id}`
2. Cierra sesión y login con un usuario del rol correcto
3. Para ADMIN: `admin@artesluis.com / admin123`

### Problema: La sesión expira rápidamente

**Causa:** Configuración de timeout de sesión

**Solución temporal:** Hacer login nuevamente

**Solución permanente:** Ajustar en `application.properties`:
```properties
server.servlet.session.timeout=30m
```

### Problema: CSRF Token requerido

**Causa:** CSRF está habilitado para ese endpoint

**Solución:** Los endpoints `/api/**` tienen CSRF deshabilitado. Para otros endpoints, obtener token CSRF primero.

---

## 📚 Recursos Adicionales

- **Documentación del Backend:** Ver `SECURITY-README.md`
- **Documentación de Render:** Ver `README-RENDER.md`

### URLs de desarrollo vs producción:

**Desarrollo Local:**
- **Swagger UI (si está instalado):** `http://localhost:8080/swagger-ui.html`
- **H2 Console (desarrollo):** `http://localhost:8080/h2-console`
- **API:** `http://localhost:8080/api/*`

**Producción (Render):**
- **API:** `https://tu-servicio.onrender.com/api/*`
- **Health Check:** `https://tu-servicio.onrender.com/api/test`
- **Dashboard de Render:** [https://dashboard.render.com/](https://dashboard.render.com/)

### Verificar que tu API en Render funciona:

**Test rápido en el navegador:**
1. Abre: `https://tu-servicio.onrender.com/api/planes`
2. Deberías ver un JSON con la lista de planes

**Test con curl:**
```bash
curl https://tu-servicio.onrender.com/api/planes
```

## 🎯 Checklist de Testing Completo

- [ ] Login exitoso como ADMIN
- [ ] Login exitoso como CLIENTE
- [ ] Login exitoso como ARTISTA
- [ ] Login con credenciales incorrectas (debe fallar)
- [ ] Acceso a endpoint público sin login
- [ ] Acceso a endpoint protegido sin login (debe fallar)
- [ ] ADMIN puede crear/editar/eliminar planes
- [ ] CLIENTE NO puede crear planes (debe fallar con 403)
- [ ] ARTISTA puede subir archivos
- [ ] CLIENTE NO puede subir archivos (debe fallar con 403)
- [ ] Logout exitoso
- [ ] Cambio de contraseña exitoso
- [ ] Actualización de perfil exitoso

---

¡Happy Testing! 🚀
