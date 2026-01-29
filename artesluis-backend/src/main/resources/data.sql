# Insertar datos de prueba automáticamente al iniciar la aplicación
# Solo para perfil de desarrollo - NO usar en producción

# Roles básicos
INSERT INTO roles (nombre) VALUES ('ADMIN') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO roles (nombre) VALUES ('CLIENTE') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO roles (nombre) VALUES ('ARTISTA') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO roles (nombre) VALUES ('MODERADOR') ON CONFLICT (nombre) DO NOTHING;

# Usuario administrador por defecto
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Admin ArteisLuis', 'admin@artesluis.com', 'admin123', NULL, r.id 
FROM roles r WHERE r.nombre = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'admin@artesluis.com');

-- Usuarios de prueba adicionales
-- Usuario Cliente 1
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'María González', 'maria@ejemplo.com', 'cliente123', NULL, r.id 
FROM roles r WHERE r.nombre = 'CLIENTE'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'maria@ejemplo.com');

-- Usuario Cliente 2
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Carlos Pérez', 'carlos@empresa.com', 'carlos456', NULL, r.id 
FROM roles r WHERE r.nombre = 'CLIENTE'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'carlos@empresa.com');

-- Usuario Cliente 3
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Ana Silva', 'ana.silva@negocio.com', 'ana789', NULL, r.id 
FROM roles r WHERE r.nombre = 'CLIENTE'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'ana.silva@negocio.com');

-- Usuario Artista 1
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Luis Artista', 'luis@artesluis.com', 'artista123', NULL, r.id 
FROM roles r WHERE r.nombre = 'ARTISTA'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'luis@artesluis.com');

-- Usuario Artista 2
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Sofia Diseño', 'sofia@artesluis.com', 'sofia456', NULL, r.id 
FROM roles r WHERE r.nombre = 'ARTISTA'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'sofia@artesluis.com');

-- Usuario Moderador 1
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Roberto Moderador', 'roberto@artesluis.com', 'mod123', NULL, r.id 
FROM roles r WHERE r.nombre = 'MODERADOR'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'roberto@artesluis.com');

-- Usuario Admin adicional
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Elena Admin', 'elena@artesluis.com', 'elena123', NULL, r.id 
FROM roles r WHERE r.nombre = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'elena@artesluis.com');

-- Usuario Cliente empresarial
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'Tech Solutions S.A.', 'contacto@techsolutions.com', 'tech2024', NULL, r.id 
FROM roles r WHERE r.nombre = 'CLIENTE'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'contacto@techsolutions.com');

-- Usuario Cliente startup
INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) 
SELECT 'InnovaStart', 'hola@innovastart.com', 'innova123', NULL, r.id 
FROM roles r WHERE r.nombre = 'CLIENTE'
AND NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'hola@innovastart.com');

-- Planes de servicios (las tablas se crean automáticamente con JPA)
INSERT INTO planes (nombre, precio, descripcion, numero_revisiones, archivos_incluidos, caracteristicas, es_recomendado, color_badge, esta_activo) VALUES 
('Básico', 99.00, 'Plan ideal para emprendedores que necesitan un diseño profesional y económico', 3, 'Archivos PNG y JPG', 'Diseño de logo;3 revisiones;Archivos PNG y JPG;Soporte por email', false, 'primary', true)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO planes (nombre, precio, descripcion, numero_revisiones, archivos_incluidos, caracteristicas, es_recomendado, color_badge, esta_activo) VALUES 
('Profesional', 199.00, 'La opción más popular para empresas que buscan una imagen corporativa completa', 5, 'Archivos PNG, JPG y vectoriales', 'Todo del plan Básico;Branding completo;5 revisiones;Archivos vectoriales;Soporte telefónico', true, 'warning', true)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO planes (nombre, precio, descripcion, numero_revisiones, archivos_incluidos, caracteristicas, es_recomendado, color_badge, esta_activo) VALUES 
('Premium', 399.00, 'Solución integral para empresas que requieren presencia digital completa', -1, 'Todos los archivos + material publicitario', 'Todo del plan Profesional;Diseño web básico;Revisiones ilimitadas;Material publicitario;Consultoría personalizada', false, 'success', true)
ON CONFLICT (nombre) DO NOTHING;