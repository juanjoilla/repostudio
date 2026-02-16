-- =====================================================
-- SCHEMA DDL PARA DIAGRAMA ERD - ARTESLUIS BACKEND
-- Base de datos: PostgreSQL
-- =====================================================
-- Este archivo contiene únicamente las definiciones de tablas (DDL)
-- para poder importar en herramientas de modelado como:
-- - dbdiagram.io
-- - draw.io
-- - MySQL Workbench
-- - DBeaver
-- - ERDPlus
-- - Lucidchart
-- =====================================================

-- Tabla: roles
-- Descripción: Catálogo de roles de usuario
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

-- Tabla: usuarios
-- Descripción: Usuarios del sistema
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    correo VARCHAR(255),
    password VARCHAR(255),
    imagen_url VARCHAR(255),
    rol_id BIGINT NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- Tabla: planes
-- Descripción: Planes de servicio disponibles
CREATE TABLE planes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(500),
    numero_revisiones INTEGER,
    archivos_incluidos VARCHAR(1000),
    caracteristicas VARCHAR(2000),
    es_recomendado BOOLEAN DEFAULT false,
    color_badge VARCHAR(50) DEFAULT 'primary',
    esta_activo BOOLEAN DEFAULT true
);

-- Tabla: bancos
-- Descripción: Catálogo de bancos para pagos por transferencia
CREATE TABLE bancos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    nombre_corto VARCHAR(255),
    logo_url VARCHAR(255),
    esta_activo BOOLEAN DEFAULT true,
    tipo_banco VARCHAR(50)
    -- ENUM: COMERCIAL, COOPERATIVO, DIGITAL, INTERNACIONAL
);

-- Tabla: carritos
-- Descripción: Carritos de compra de sesión
CREATE TABLE carritos (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Tabla: items_carrito
-- Descripción: Items dentro de un carrito
CREATE TABLE items_carrito (
    id BIGSERIAL PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_carrito FOREIGN KEY (carrito_id) REFERENCES carritos(id),
    CONSTRAINT fk_item_plan FOREIGN KEY (plan_id) REFERENCES planes(id)
);

-- Tabla: ordenes
-- Descripción: Órdenes de compra generadas
CREATE TABLE ordenes (
    id BIGSERIAL PRIMARY KEY,
    numero_orden VARCHAR(255) UNIQUE NOT NULL,
    usuario_id BIGINT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) DEFAULT 0,
    estado VARCHAR(50),
    -- ENUM: PENDIENTE, PAGADO, EN_PROCESO, COMPLETADO, CANCELADO, REEMBOLSADO
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    email_cliente VARCHAR(255),
    telefono_cliente VARCHAR(255),
    nombre_cliente VARCHAR(255),
    notas TEXT,
    CONSTRAINT fk_orden_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla: detalles_orden
-- Descripción: Detalles de los items en una orden
CREATE TABLE detalles_orden (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    descuento_unitario DECIMAL(10,2) DEFAULT 0,
    nombre_plan VARCHAR(255),
    descripcion_plan VARCHAR(1000),
    CONSTRAINT fk_detalle_orden FOREIGN KEY (orden_id) REFERENCES ordenes(id),
    CONSTRAINT fk_detalle_plan FOREIGN KEY (plan_id) REFERENCES planes(id)
);

-- Tabla: pagos
-- Descripción: Pagos realizados para las órdenes
CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL,
    referencia_pago VARCHAR(255) UNIQUE,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50),
    -- ENUM: TARJETA_CREDITO, TARJETA_DEBITO, PAYPAL, TRANSFERENCIA_BANCARIA, 
    --       EFECTIVO, MERCADO_PAGO, STRIPE, SERVICIO_DIGITAL, REEMBOLSO, OTRO
    estado VARCHAR(50),
    -- ENUM: PENDIENTE, PROCESANDO, COMPLETADO, FALLIDO, CANCELADO, 
    --       REEMBOLSADO, PARCIALMENTE_REEMBOLSADO
    fecha_pago TIMESTAMP,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    gateway_transaction_id VARCHAR(255),
    gateway_response VARCHAR(1000),
    gateway_status VARCHAR(255),
    numero_tarjeta_ultimos_4 VARCHAR(4),
    tipo_tarjeta VARCHAR(50),
    nombre_tarjetahabiente VARCHAR(255),
    notas TEXT,
    banco_id BIGINT,
    numero_cuenta_origen VARCHAR(255),
    numero_referencia_transferencia VARCHAR(255),
    CONSTRAINT fk_pago_orden FOREIGN KEY (orden_id) REFERENCES ordenes(id),
    CONSTRAINT fk_pago_banco FOREIGN KEY (banco_id) REFERENCES bancos(id)
);

-- =====================================================
-- ÍNDICES RECOMENDADOS
-- =====================================================
CREATE INDEX idx_usuarios_correo ON usuarios(correo);
CREATE INDEX idx_usuarios_rol_id ON usuarios(rol_id);
CREATE INDEX idx_carritos_session_id ON carritos(session_id);
CREATE INDEX idx_ordenes_usuario_id ON ordenes(usuario_id);
CREATE INDEX idx_ordenes_numero_orden ON ordenes(numero_orden);
CREATE INDEX idx_ordenes_estado ON ordenes(estado);
CREATE INDEX idx_pagos_orden_id ON pagos(orden_id);
CREATE INDEX idx_pagos_estado ON pagos(estado);
CREATE INDEX idx_detalles_orden_id ON detalles_orden(orden_id);
CREATE INDEX idx_items_carrito_id ON items_carrito(carrito_id);

-- =====================================================
-- COMENTARIOS DE DOCUMENTACIÓN
-- =====================================================
COMMENT ON TABLE roles IS 'Catálogo de roles de usuario del sistema';
COMMENT ON TABLE usuarios IS 'Usuarios registrados en la plataforma';
COMMENT ON TABLE planes IS 'Planes de servicio de diseño disponibles para compra';
COMMENT ON TABLE bancos IS 'Catálogo de entidades bancarias para transferencias';
COMMENT ON TABLE carritos IS 'Carritos de compra por sesión';
COMMENT ON TABLE items_carrito IS 'Ítems agregados al carrito de compra';
COMMENT ON TABLE ordenes IS 'Órdenes de compra generadas por los clientes';
COMMENT ON TABLE detalles_orden IS 'Líneas de detalle de cada orden de compra';
COMMENT ON TABLE pagos IS 'Pagos realizados asociados a órdenes';
