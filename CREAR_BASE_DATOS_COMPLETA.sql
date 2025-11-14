-- ============================================================================
-- SCRIPT COMPLETO PARA CREAR LA BASE DE DATOS DEL SISTEMA DE BIBLIOTECA
-- Ejecuta TODO este archivo en MySQL Workbench
-- ============================================================================

-- Eliminar la base de datos si existe (para empezar limpio)
DROP DATABASE IF EXISTS biblioteca_db;

-- Crear la base de datos
CREATE DATABASE biblioteca_db;

-- Usar la base de datos
USE biblioteca_db;

-- ============================================================================
-- CREAR TABLAS
-- ============================================================================

-- Tabla de Usuarios (Administradores, Profesores, Alumnos)
CREATE TABLE usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    tipo_usuario ENUM('Administrador', 'Profesor', 'Alumno') NOT NULL,
    tiene_mora BOOLEAN DEFAULT FALSE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Tipos de Documentos
CREATE TABLE tipos_documento (
    id_tipo INT PRIMARY KEY AUTO_INCREMENT,
    nombre_tipo VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(200)
);

-- Tabla de Documentos (Libros, Revistas, Tesis, CD, etc.)
CREATE TABLE documentos (
    id_documento INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(150),
    id_tipo INT NOT NULL,
    anio_publicacion INT,
    editorial VARCHAR(100),
    isbn VARCHAR(50),
    cantidad_total INT DEFAULT 1,
    cantidad_disponible INT DEFAULT 1,
    ubicacion_fisica VARCHAR(100),
    es_prestable BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_tipo) REFERENCES tipos_documento(id_tipo)
);

-- Tabla de Préstamos
CREATE TABLE prestamos (
    id_prestamo INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_documento INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado ENUM('Activo', 'Devuelto', 'Atrasado') DEFAULT 'Activo',
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_documento) REFERENCES documentos(id_documento)
);

-- Tabla de Configuración de Préstamos
CREATE TABLE configuracion_prestamos (
    id_config INT PRIMARY KEY AUTO_INCREMENT,
    max_ejemplares_prestables INT DEFAULT 3,
    dias_prestamo INT DEFAULT 7,
    mora_diaria DECIMAL(10,2) DEFAULT 0.50
);

-- Tabla de Moras
CREATE TABLE moras (
    id_mora INT PRIMARY KEY AUTO_INCREMENT,
    id_prestamo INT NOT NULL,
    dias_mora INT NOT NULL,
    monto_mora DECIMAL(10,2) NOT NULL,
    pagado BOOLEAN DEFAULT FALSE,
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_prestamo) REFERENCES prestamos(id_prestamo)
);

-- ============================================================================
-- INSERTAR DATOS DE PRUEBA
-- ============================================================================

-- Insertar tipos de documentos predefinidos
INSERT INTO tipos_documento (nombre_tipo, descripcion) VALUES
('Libro', 'Libros de texto, novelas, etc.'),
('Revista', 'Publicaciones periódicas'),
('Tesis', 'Trabajos de investigación universitaria'),
('CD', 'Discos compactos con contenido multimedia'),
('Documento', 'Documentos varios de información');

-- Insertar configuración inicial de préstamos
INSERT INTO configuracion_prestamos (max_ejemplares_prestables, dias_prestamo, mora_diaria)
VALUES (3, 7, 0.50);

-- Insertar usuario administrador por defecto (password: admin123)
INSERT INTO usuarios (nombre, username, password, tipo_usuario, tiene_mora)
VALUES ('Administrador', 'admin', 'admin123', 'Administrador', FALSE);

-- Insertar algunos usuarios de prueba
INSERT INTO usuarios (nombre, username, password, tipo_usuario, tiene_mora) VALUES
('Juan Pérez', 'jperez', 'profesor123', 'Profesor', FALSE),
('María García', 'mgarcia', 'alumno123', 'Alumno', FALSE),
('Carlos López', 'clopez', 'alumno123', 'Alumno', FALSE);

-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================

-- Mostrar las tablas creadas
SHOW TABLES;

-- Mostrar los usuarios insertados
SELECT * FROM usuarios;

-- Mostrar los tipos de documentos
SELECT * FROM tipos_documento;

-- Mostrar la configuración de préstamos
SELECT * FROM configuracion_prestamos;

-- ============================================================================
-- ¡LISTO! La base de datos está creada y lista para usar
-- ============================================================================
