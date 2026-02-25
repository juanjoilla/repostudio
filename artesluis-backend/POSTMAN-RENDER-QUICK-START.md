# 🚀 Quick Start: Probar API en Render con Postman

## ✅ Paso 1: Obtener tu URL de Render

1. Ve a [https://dashboard.render.com/](https://dashboard.render.com/)
2. Inicia sesión con tu cuenta
3. Verás tu servicio `artesluis-backend` (o similar)
4. **Copia la URL completa** que aparece arriba. Ejemplo:
   ```
   https://artesluis-backend-xxxx.onrender.com
   ```
5. 🚨 **IMPORTANTE:** NO agregues puertos (`:8080` o `:10000`) a esta URL

---

## ✅ Paso 2: Verificar que la API funciona

### Opción A: En el navegador
Abre en tu navegador:
```
https://tu-servicio.onrender.com/api/planes
```

**Respuesta esperada:** JSON con lista de planes  
**Si ves JSON:** ✅ Tu API está funcionando correctamente

### Opción B: En Postman (Test rápido)
1. Abre Postman
2. Crea un nuevo request
3. Método: `GET`
4. URL: `https://tu-servicio.onrender.com/api/planes`
5. Click "Send"

**Respuesta esperada:** 200 OK con JSON

---

## ✅ Paso 3: Configurar Postman para toda la colección

### 3.1 Crear Environment

1. En Postman, haz clic en "Environments" (⚙️) en la barra lateral
2. Click "+" para crear nuevo environment
3. Nombre: `Render - Producción`
4. Agrega variable:
   - **Variable:** `baseUrl`
   - **Initial Value:** `https://tu-servicio.onrender.com`
   - **Current Value:** `https://tu-servicio.onrender.com`
5. Click "Save"

### 3.2 Activar el Environment

1. En la esquina superior derecha de Postman
2. Dropdown que dice "No Environment"
3. Selecciona: `Render - Producción`

### 3.3 Usar en tus requests

Ahora en cualquier request, usa:
```
{{baseUrl}}/api/planes
{{baseUrl}}/login
{{baseUrl}}/api/admin/usuarios
```

Postman reemplazará automáticamente `{{baseUrl}}` con tu URL de Render.

---

## ✅ Paso 4: Hacer Login

### 4.1 Request de Login

**Crear nuevo request:**
- Método: `POST`
- URL: `{{baseUrl}}/login`
- Headers:
  ```
  Content-Type: application/x-www-form-urlencoded
  ```
- Body → seleccionar `x-www-form-urlencoded`
- Agregar:
  ```
  correo: admin@artesluis.com
  password: admin123
  ```

### 4.2 Configurar Cookies (MUY IMPORTANTE)

**Antes de hacer login:**
1. Ve a Settings (⚙️) → General
2. Busca "Cookie" o "Interceptor"
3. Asegúrate que "Interceptor" o "Postman Agent" esté **ON**

**Alternativa:**
1. Click en el ícono 🍪 (Cookies) debajo de "Send"
2. Agregar dominio: Tu dominio de Render (ej: `artesluis-backend-xxxx.onrender.com`)

### 4.3 Enviar Login

1. Click "Send"
2. Si todo funciona, verás respuesta `302` o `200`
3. Postman guardará automáticamente la cookie `JSESSIONID`

### 4.4 Verificar Cookie

1. Click en 🍪 (Cookies) debajo de "Send"
2. Busca tu dominio de Render
3. Deberías ver una cookie llamada `JSESSIONID`
4. ✅ Si la ves, **estás autenticado**

---

## ✅ Paso 5: Probar endpoint protegido

**Crear nuevo request:**
- Método: `GET`
- URL: `{{baseUrl}}/api/admin/usuarios`
- Headers: (ninguno adicional, Postman enviará la cookie automáticamente)
- Click "Send"

**Respuesta esperada:**
- 200 OK con lista de usuarios (si eres ADMIN)
- 403 Forbidden (si no tienes permisos)
- 401 Unauthorized (si no hay sesión activa)

---

## 🔥 Solución de Problemas Comunes

### ❌ Error: "Could not get any response" o timeout

**Causas posibles:**
1. El servicio de Render está dormido (plan gratuito se duerme tras 15 min de inactividad)
2. La URL es incorrecta

**Solución:**
1. Abre la URL en el navegador primero: `https://tu-servicio.onrender.com/api/planes`
2. Espera 1-2 minutos (Render está despertando el servicio)
3. Intenta nuevamente en Postman

### ❌ Error: "401 Unauthorized" en endpoints protegidos

**Causa:** No has hecho login o la sesión expiró

**Solución:**
1. Haz login primero (`POST {{baseUrl}}/login`)
2. Verifica que Postman guardó la cookie (🍪)
3. Intenta el endpoint protegido nuevamente

### ❌ Error: "403 Forbidden"

**Causa:** Tu usuario no tiene el rol necesario

**Solución:**
1. Verifica con qué usuario hiciste login
2. Para probar endpoints de ADMIN, usa: `admin@artesluis.com / admin123`
3. Haz logout (`POST {{baseUrl}}/logout`) y login con el usuario correcto

### ❌ "No puedo hacer login"

**Verifica:**
1. URL correcta: `{{baseUrl}}/login` (no `/api/login`)
2. Método: POST (no GET)
3. Content-Type: `application/x-www-form-urlencoded`
4. Body debe tener: `correo` y `password` (no `email` o `username`)

---

## 📝 Checklist rápido

- [ ] Tengo la URL pública de mi servicio de Render
- [ ] No estoy usando `localhost` ni puerto `:10000`
- [ ] Creé el environment en Postman con `baseUrl`
- [ ] Activé el environment en Postman
- [ ] Configuré Postman para manejar cookies
- [ ] Puedo acceder a `/api/planes` sin login
- [ ] Puedo hacer login exitosamente
- [ ] Postman guardó mi cookie JSESSIONID
- [ ] Puedo acceder a endpoints protegidos

---

## 🎯 Test Rápido Final

Copia estos requests en Postman para validar todo:

**1. Health Check (público):**
```
GET {{baseUrl}}/api/planes
```
✅ Debe funcionar sin login

**2. Login:**
```
POST {{baseUrl}}/login
Body: correo=admin@artesluis.com&password=admin123
Content-Type: application/x-www-form-urlencoded
```
✅ Debe responder 200 o 302

**3. Endpoint protegido:**
```
GET {{baseUrl}}/api/admin/usuarios
```
✅ Debe responder 200 con lista de usuarios (si hiciste login)

---

## 📞 Necesitas más ayuda?

- **POSTMAN-TESTING-GUIDE.md**: Guía completa de todos los endpoints
- **README-RENDER.md**: Documentación de despliegue en Render
- **SECURITY-README.md**: Documentación de seguridad y autenticación
