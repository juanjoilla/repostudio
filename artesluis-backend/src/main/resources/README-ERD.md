# 📊 Esquema de Base de Datos - ArtesLuis Backend

Este directorio contiene los scripts de definición del esquema de base de datos para generar diagramas ERD (Entity-Relationship Diagram).

## 📁 Archivos Disponibles

### 1. `schema-erd.sql`
Script DDL en PostgreSQL con todas las definiciones de tablas, relaciones, índices y comentarios.

**Uso:**
- Compatible con herramientas como DBeaver, pgAdmin, MySQL Workbench
- Se puede importar directamente en una base de datos PostgreSQL
- Útil para generar diagramas usando reverse engineering

### 2. `schema-erd.dbml`
Esquema en formato DBML (Database Markup Language) - formato declarativo y visual.

**Uso:**
1. Visita [https://dbdiagram.io/d](https://dbdiagram.io/d)
2. Copia y pega el contenido del archivo `schema-erd.dbml`
3. El diagrama se generará automáticamente
4. Puedes exportar como PNG, PDF o SQL

## 🗂️ Estructura de la Base de Datos

El sistema cuenta con **9 tablas principales**:

### Tablas de Catálogo
- **`roles`**: Roles de usuario (ADMIN, CLIENTE, ARTISTA, MODERADOR)
- **`bancos`**: Catálogo de bancos para transferencias bancarias
- **`planes`**: Planes de servicio (Básico, Profesional, Premium)

### Tablas de Usuarios
- **`usuarios`**: Usuarios registrados en la plataforma

### Tablas de Carrito
- **`carritos`**: Carritos de compra por sesión
- **`items_carrito`**: Items dentro de cada carrito

### Tablas de Órdenes y Pagos
- **`ordenes`**: Órdenes de compra generadas
- **`detalles_orden`**: Líneas de detalle de cada orden
- **`pagos`**: Pagos realizados (múltiples métodos de pago)

## 🔗 Relaciones Principales

```
roles (1) ──────< (N) usuarios
usuarios (1) ────< (N) ordenes
ordenes (1) ─────< (N) detalles_orden
ordenes (1) ─────< (N) pagos
planes (1) ──────< (N) detalles_orden
planes (1) ──────< (N) items_carrito
carritos (1) ────< (N) items_carrito
bancos (1) ──────< (N) pagos
```

## 🛠️ Herramientas Recomendadas para Generar Diagramas ERD

### Online (Gratis)
1. **dbdiagram.io** ⭐ Recomendado
   - Usa el archivo `.dbml`
   - Interfaz limpia y moderna
   - Exporta a PNG, PDF, SQL
   - URL: https://dbdiagram.io

2. **QuickDBD**
   - Sintaxis simple
   - URL: https://www.quickdatabasediagrams.com

3. **draw.io (diagrams.net)**
   - Más manual pero muy flexible
   - URL: https://app.diagrams.net

### Desktop (Software)
1. **DBeaver** ⭐ Recomendado
   - Importa el archivo `.sql`
   - Genera diagramas automáticamente
   - Gratis y Open Source

2. **MySQL Workbench**
   - Reverse engineering desde SQL
   - También funciona con PostgreSQL

3. **pgAdmin 4**
   - Específico para PostgreSQL
   - Incluye herramientas de visualización

## 📝 Características del Esquema

### Enumeraciones (ENUM)
El sistema utiliza varios tipos enumerados:

**Rol.nombre:**
- ADMIN
- CLIENTE
- ARTISTA
- MODERADOR

**Banco.tipo_banco:**
- COMERCIAL
- COOPERATIVO
- DIGITAL
- INTERNACIONAL

**Orden.estado:**
- PENDIENTE
- PAGADO
- EN_PROCESO
- COMPLETADO
- CANCELADO
- REEMBOLSADO

**Pago.metodo_pago:**
- TARJETA_CREDITO
- TARJETA_DEBITO
- PAYPAL
- TRANSFERENCIA_BANCARIA
- MERCADO_PAGO
- STRIPE
- EFECTIVO
- SERVICIO_DIGITAL
- REEMBOLSO
- OTRO

**Pago.estado:**
- PENDIENTE
- PROCESANDO
- COMPLETADO
- FALLIDO
- CANCELADO
- REEMBOLSADO
- PARCIALMENTE_REEMBOLSADO

## 🔍 Índices Optimizados

El esquema incluye índices para mejorar el rendimiento:
- `usuarios.correo` - Búsquedas de login
- `ordenes.numero_orden` - Búsqueda de órdenes
- `ordenes.estado` - Filtros por estado
- `pagos.estado` - Filtros de pagos
- Índices en todas las foreign keys

## 💡 Notas Técnicas

- **Motor de BD**: PostgreSQL 12+
- **ORM**: JPA/Hibernate
- **Estrategia de generación de IDs**: `IDENTITY` (auto-increment)
- **Precisión decimal**: 10,2 para campos monetarios
- **Timestamps**: `LocalDateTime` (sin zona horaria)

## 🚀 Generar el Diagrama Rápidamente

### Opción 1: dbdiagram.io (Más rápido)
```bash
# 1. Abrir https://dbdiagram.io/d
# 2. Copiar contenido de schema-erd.dbml
# 3. Pegar en el editor
# 4. ¡Listo! Exportar como imagen
```

### Opción 2: DBeaver (Más detallado)
```bash
# 1. Abrir DBeaver
# 2. Crear conexión PostgreSQL local
# 3. Ejecutar schema-erd.sql
# 4. Click derecho en base de datos > View Diagram
# 5. Exportar como PNG/PDF
```

## 📞 Soporte

Para preguntas sobre el esquema:
- Revisar los comentarios en los archivos SQL/DBML
- Consultar las anotaciones JPA en las entidades Java
- Ver la documentación del modelo en `/src/main/java/com/artesluis/artesluis_backend/model/`

---

**Última actualización**: Enero 2026  
**Versión del esquema**: 1.0.0
