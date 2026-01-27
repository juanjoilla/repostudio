-- ============================================
-- Script de datos de prueba para ArteisLuis
-- ============================================

-- Insertar roles de prueba
INSERT INTO roles (nombre) VALUES 
('ADMIN'),
('CLIENTE'),
('ARTISTA'),
('MODERADOR');

-- Insertar usuarios de prueba
-- (Nota: Los passwords están en texto plano para pruebas, en producción deberían estar encriptados)

INSERT INTO usuarios (nombre, correo, password, imagen_url, rol_id) VALUES 
-- Administradores
('Luis Artista', 'luis@artesluis.com', 'admin123', 'https://example.com/images/luis.jpg', 1),
('Admin Sistema', 'admin@artesluis.com', 'system456', NULL, 1),

-- Artistas
('María González', 'maria.gonzalez@email.com', 'artista123', 'https://example.com/images/maria.jpg', 3),
('Carlos Pérez', 'carlos.perez@email.com', 'carlos789', 'https://example.com/images/carlos.jpg', 3),
('Ana Rodríguez', 'ana.rodriguez@email.com', 'ana456', NULL, 3),

-- Clientes
('Juan Cliente', 'juan.cliente@email.com', 'cliente123', NULL, 2),
('Elena Martínez', 'elena.martinez@email.com', 'elena456', 'https://example.com/images/elena.jpg', 2),
('Roberto Silva', 'roberto.silva@email.com', 'roberto789', NULL, 2),
('Carmen López', 'carmen.lopez@email.com', 'carmen123', 'https://example.com/images/carmen.jpg', 2),
('David Fernández', 'david.fernandez@email.com', 'david456', NULL, 2),

-- Moderadores
('Mónica Moderadora', 'monica.mod@artesluis.com', 'mod123', 'https://example.com/images/monica.jpg', 4),
('Pedro Supervisor', 'pedro.supervisor@artesluis.com', 'super456', NULL, 4);

-- Verificar que los datos se insertaron correctamente
-- SELECT r.nombre as rol, u.nombre, u.correo FROM usuarios u 
-- JOIN roles r ON u.rol_id = r.id 
-- ORDER BY r.id, u.nombre;

-- Conteo por roles
-- SELECT r.nombre as rol, COUNT(u.id) as cantidad_usuarios 
-- FROM roles r 
-- LEFT JOIN usuarios u ON r.id = u.rol_id 
-- GROUP BY r.id, r.nombre 
-- ORDER BY r.id;