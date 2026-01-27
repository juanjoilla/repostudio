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