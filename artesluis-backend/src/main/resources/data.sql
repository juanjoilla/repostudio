# Insertar datos de prueba automáticamente al iniciar la aplicación
# Solo para perfil de desarrollo - NO usar en producción

# Roles básicos - Con verificación de existencia
INSERT INTO roles (nombre) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ADMIN');
INSERT INTO roles (nombre) SELECT 'CLIENTE' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'CLIENTE');
INSERT INTO roles (nombre) SELECT 'ARTISTA' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ARTISTA');
INSERT INTO roles (nombre) SELECT 'MODERADOR' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'MODERADOR');

INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Admin ArteisLuis', 'admin@artesluis.com', 'admin123', NULL, 1
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'admin@artesluis.com');

INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Juan Illatopa', 'Juan@utp.com', 'juan123', NULL, 2
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'juan@utp.com');

INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'María González', 'maria@ejemplo.com', 'cliente123', NULL, 2
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'maria@ejemplo.com');

INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Ana Silva', 'ana.silva@negocio.com', 'ana789', NULL, 2
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'ana.silva@negocio.com');

-- Planes de servicios (las tablas se crean automáticamente con JPA) - H2 compatible
INSERT INTO planes (nombre, precio, descripcion, numero_revisiones, archivos_incluidos, caracteristicas, es_recomendado, color_badge, esta_activo) VALUES 
('Básico', 99.00, 'Plan ideal para emprendedores que necesitan un diseño profesional y económico', 3, 'Archivos PNG y JPG', 'Diseño de logo;3 revisiones;Archivos PNG y JPG;Soporte por email', false, 'primary', true);

INSERT INTO planes (nombre, precio, descripcion, numero_revisiones, archivos_incluidos, caracteristicas, es_recomendado, color_badge, esta_activo) VALUES 
('Profesional', 199.00, 'La opción más popular para empresas que buscan una imagen corporativa completa', 5, 'Archivos PNG, JPG y vectoriales', 'Todo del plan Básico;Branding completo;5 revisiones;Archivos vectoriales;Soporte telefónico', true, 'warning', true);

INSERT INTO planes (nombre, precio, descripcion, numero_revisiones, archivos_incluidos, caracteristicas, es_recomendado, color_badge, esta_activo) VALUES 
('Premium', 399.00, 'Solución integral para empresas que requieren presencia digital completa', -1, 'Todos los archivos + material publicitario', 'Todo del plan Profesional;Diseño web básico;Revisiones ilimitadas;Material publicitario;Consultoría personalizada', false, 'success', true);

-- Bancos para transferencias bancarias (datos de Colombia como ejemplo)
INSERT INTO bancos (codigo, nombre, nombre_corto, tipo_banco, esta_activo) VALUES 
('001', 'Banco de Bogotá', 'Bogotá', 'COMERCIAL', true),
('002', 'Banco Popular', 'Popular', 'COMERCIAL', true),
('007', 'Bancolombia', 'Bancolombia', 'COMERCIAL', true),
('013', 'BBVA Colombia', 'BBVA', 'COMERCIAL', true),
('019', 'Scotiabank Colpatria', 'Colpatria', 'COMERCIAL', true),
('023', 'Banco de Occidente', 'Occidente', 'COMERCIAL', true),
('040', 'Banco Agrario', 'Agrario', 'COMERCIAL', true),
('051', 'Banco Davivienda', 'Davivienda', 'COMERCIAL', true),
('052', 'Banco AV Villas', 'AV Villas', 'COMERCIAL', true),
('059', 'Bancamía', 'Bancamía', 'COMERCIAL', true),
('062', 'Banco Falabella', 'Falabella', 'COMERCIAL', true),
('066', 'Banco Cooperativo Coopcentral', 'Coopcentral', 'COOPERATIVO', true),
('283', 'Cooperativa Financiera de Antioquia', 'CFA', 'COOPERATIVO', true),
('289', 'Cotrafa Cooperativa Financiera', 'Cotrafa', 'COOPERATIVO', true),
('370', 'Coltefinanciera', 'Coltefinanciera', 'COMERCIAL', true),
('558', 'Banco Credifinanciera', 'Credifinanciera', 'COMERCIAL', true),
('801', 'Nequi (Bancolombia)', 'Nequi', 'DIGITAL', true),
('802', 'Daviplata (Davivienda)', 'Daviplata', 'DIGITAL', true);

-- Carrito de prueba para el usuario Carlos (rol_id = 2)
-- Primero insertar un carrito con session_id simulado
INSERT INTO carritos (session_id, fecha_creacion, fecha_actualizacion) VALUES 
('test-session-carlos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Luego insertar items en el carrito (asumiendo que el carrito tiene ID 1)
INSERT INTO items_carrito (carrito_id, plan_id, cantidad, precio_unitario) VALUES 
(1, 2, 1, 199.00),  -- Plan Profesional
(1, 3, 1, 399.00);  -- Plan Premium