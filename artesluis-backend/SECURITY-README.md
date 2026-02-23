# Implementación de Seguridad con Spring Security

## Resumen de Cambios

Se ha implementado **Spring Security** para proteger las APIs y endpoints de la aplicación según los roles de usuario. La seguridad ahora se gestiona de forma automática y centralizada.

## Roles del Sistema

- **ADMIN**: Acceso total a todas las funcionalidades administrativas
- **MODERADOR**: Acceso a gestión de usuarios y algunas funciones administrativas
- **ARTISTA**: Puede subir archivos y contenido
- **CLIENTE**: Usuario regular con acceso a funcionalidades públicas y su perfil

## Endpoints Públicos (No requieren autenticación)

### Páginas Web
- `/`, `/index`, `/login`, `/logout`
- `/contacto`, `/nosotros`, `/mision`, `/portafolio`
- `/planes` (vista)
- Recursos estáticos: `/static/**`, `/css/**`, `/js/**`, `/img/**`, `/uploads/**`

### APIs Públicas
- `POST /api/usuarios/login` - Iniciar sesión
- `POST /api/usuarios/registro` - Registrar nuevo usuario
- `GET /api/data/stats` - Estadísticas públicas
- `GET /health`, `/api/health/**` - Health checks
- `GET /api/planes` - Listar planes (solo lectura)

## Endpoints Protegidos

### Usuarios Autenticados (Cualquier rol)
- `GET /api/usuarios/perfil/{id}` - Obtener perfil
- `PUT /api/usuarios/perfil/{id}` - Actualizar perfil
- `PUT /api/usuarios/cambiar-password/{id}` - Cambiar contraseña
- `/carrito/**` - Gestión del carrito de compras
- `/checkout/**` - Proceso de compra
- `/mis-ordenes` - Ver mis órdenes

### Solo ADMIN
- `/admin/**` - Panel de administración
- `/api/admin/**` - APIs administrativas
- `POST /api/planes` - Crear plan
- `PUT /api/planes/{id}` - Actualizar plan
- `DELETE /api/planes/{id}` - Eliminar plan
- `/api/roles/**` - Gestión de roles
- `/api/admin/ordenes/**` - Gestión de órdenes

### ADMIN o MODERADOR
- `GET /api/admin/usuarios` - Listar usuarios
- `/api/usuarios/**` (excepto login/registro/perfil) - Gestión de usuarios

### ADMIN o ARTISTA
- `/api/upload/**` - Subir archivos e imágenes

## Cambios Técnicos Implementados

### 1. Dependencias Agregadas
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2. Clases de Seguridad Creadas

#### `CustomUserDetails`
Implementación de `UserDetails` que envuelve el modelo `Usuario`.

#### `CustomUserDetailsService`
Servicio que carga usuarios desde la base de datos para Spring Security.

#### `CustomAuthenticationSuccessHandler`
Handler que establece atributos de sesión después del login exitoso para mantener compatibilidad con código existente.

#### `SecurityConfig`
Configuración principal de Spring Security con:
- Autenticación basada en formulario
- Autorización por URL patterns
- Gestión de sesiones
- CSRF deshabilitado para APIs REST

### 3. Controladores Actualizados

Todos los controladores sensibles ahora usan anotaciones `@PreAuthorize` en lugar de verificaciones manuales de sesión:

- **AdminPlanesController**: `@PreAuthorize("hasRole('ADMIN')")` en métodos CRUD
- **AdminOrdenesController**: `@PreAuthorize("hasRole('ADMIN')")` a nivel de clase
- **RolController**: `@PreAuthorize("hasRole('ADMIN')")` a nivel de clase
- **UploadController**: `@PreAuthorize("hasAnyRole('ADMIN', 'ARTISTA')")`
- **AdminController**: `@PreAuthorize("hasAnyRole('ADMIN', 'MODERADOR')")`
- **UsuarioController**: `@PreAuthorize("isAuthenticated()")` en endpoints de perfil

### 4. Simplificación de Validaciones

**ANTES:**
```java
@PostMapping("/api/planes")
public ResponseEntity<Plan> crearPlan(@RequestBody Plan plan, HttpSession session) {
    if (session.getAttribute("usuario") == null || 
        !"ADMIN".equals(session.getAttribute("rolUsuario"))) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    // ... lógica del método
}
```

**DESPUÉS:**
```java
@PostMapping("/api/planes")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Plan> crearPlan(@RequestBody Plan plan) {
    // Spring Security valida automáticamente
    // ... lógica del método
}
```

## Compatibilidad con Código Existente

La implementación mantiene compatibilidad con el código existente que usa sesiones HTTP:
- Los atributos de sesión (`usuario`, `usuarioId`, `rolUsuario`) se siguen estableciendo
- Las vistas web que verifican sesión seguirán funcionando
- El flujo de login existente se mantiene

## Configuración de Login

### Formulario de Login
El formulario HTML debe enviar a `/login` con los campos:
- `correo`: Email del usuario (no "username")
- `password`: Contraseña

### Login Programático (APIs)
Para APIs que necesiten autenticación, se recomienda:
1. Hacer POST a `/login` con las credenciales
2. Spring Security establece la sesión automáticamente
3. Las peticiones subsiguientes incluirán la cookie de sesión

## Seguridad de Contraseñas

⚠️ **IMPORTANTE - ACCIÓN REQUERIDA:**

Actualmente, las contraseñas se almacenan en **texto plano** usando `NoOpPasswordEncoder` para mantener compatibilidad con los datos existentes.

### Migración Recomendada para Producción:

1. Actualizar `SecurityConfig` para usar `BCryptPasswordEncoder`:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

2. Crear script de migración para re-hashear todas las contraseñas:
```java
String hashedPassword = new BCryptPasswordEncoder().encode(plainPassword);
usuario.setPassword(hashedPassword);
```

3. Actualizar proceso de registro para hashear contraseñas automáticamente

## Testing

### Probar Endpoints Públicos
```bash
curl http://localhost:8080/api/planes
```

### Probar Endpoints Protegidos
Debe retornar 401 Unauthorized o redirigir al login:
```bash
curl http://localhost:8080/api/admin/usuarios
```

### Login y Acceso
```bash
# 1. Login
curl -X POST http://localhost:8080/login \
  -d "correo=admin@artesluis.com&password=admin123" \
  -c cookies.txt

# 2. Usar sesión
curl http://localhost:8080/api/admin/usuarios \
  -b cookies.txt
```

## Ventajas de la Implementación

✅ **Centralizada**: Toda la seguridad en un solo lugar (SecurityConfig)  
✅ **Declarativa**: Anotaciones claras en cada endpoint  
✅ **Mantenible**: Fácil de entender y modificar  
✅ **Estándar**: Usa Spring Security, el framework estándar de la industria  
✅ **Compatible**: Mantiene compatibilidad con código existente  
✅ **Escalable**: Fácil agregar nuevos roles o permisos

## Próximos Pasos Recomendados

1. **Migrar a contraseñas hasheadas** (BCrypt)
2. **Implementar JWT** para APIs RESTful stateless (opcional)
3. **Agregar HTTPS** en producción
4. **Implementar rate limiting** para prevenir ataques
5. **Agregar auditoría** de accesos y cambios
6. **Habilitar CSRF** para formularios web en producción

## Soporte

Para preguntas o problemas con la seguridad, consultar:
- Documentación de Spring Security: https://spring.io/projects/spring-security
- Guías de seguridad: https://spring.io/guides/gs/securing-web/
