-- ============================================================================
-- SCRIPT COMPLETO PARA CREAR LA BASE DE DATOS DEL SISTEMA DE BIBLIOTECA
-- Ejecuta TODO este archivo en MySQL Workbench
-- ============================================================================

DROP DATABASE IF EXISTS biblioteca;

CREATE DATABASE biblioteca;

USE biblioteca;

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


-- Usamos la base de datos 'biblioteca'
USE biblioteca;

-- ============================================================================
-- INSERTAR DOCUMENTOS DE PRUEBA (Libros, Revistas, Tesis, etc.)
-- ============================================================================
-- Nota:
-- id_tipo = 1 es 'Libro'
-- id_tipo = 2 es 'Revista'
-- id_tipo = 3 es 'Tesis'
-- id_tipo = 4 es 'CD'
-- id_tipo = 5 es 'Documento'
-- ============================================================================

INSERT INTO documentos (
    titulo,
    autor,
    id_tipo, -- El ID de la tabla 'tipos_documento'
    anio_publicacion,
    editorial,
    isbn,
    cantidad_total,
    cantidad_disponible,
    ubicacion_fisica
) VALUES
-- Libros (id_tipo = 1)
('Cien Años de Soledad', 'Gabriel García Márquez', 1, 1967, 'Sudamericana', '978-030735044X', 5, 5, 'F-A-01'),
('1984', 'George Orwell', 1, 1949, 'Secker & Warburg', '978-0451524935', 4, 4, 'F-B-02'),
('El Señor de los Anillos: La Comunidad del Anillo', 'J.R.R. Tolkien', 1, 1954, 'Allen & Unwin', '978-0618640157', 3, 3, 'F-C-03'),
('Harry Potter y la Piedra Filosofal', 'J.K. Rowling', 1, 1997, 'Bloomsbury', '978-0747532699', 10, 10, 'F-J-01'),
('Cálculo: Trascendentes Tempranas', 'James Stewart', 1, 2015, 'Cengage', '978-1305266643', 8, 8, 'CI-A-11'),
('Física para la ciencia y la tecnología', 'Paul Tipler', 1, 2009, 'Reverté', '978-8429144291', 5, 5, 'CI-B-12'),
('Introducción a la Programación con Java', 'Paul Deitel', 1, 2017, 'Pearson', '978-0134743356', 6, 6, 'CI-C-13'),
('Don Quijote de la Mancha', 'Miguel de Cervantes', 1, 1605, 'Francisco de Robles', '978-8424117631', 3, 3, 'CL-A-01'),

-- Revistas (id_tipo = 2)
('National Geographic (Octubre 2025)', 'Varios', 2, 2025, 'NatGeo Society', NULL, 15, 15, 'R-01'),
('Scientific American (Noviembre 2025)', 'Varios', 2, 2025, 'Springer Nature', NULL, 12, 12, 'R-02'),
('Harvard Business Review (Q4 2025)', 'Varios', 2, 2025, 'HBR', NULL, 10, 10, 'R-03'),

-- Tesis (id_tipo = 3)
('Impacto de la IA en el Diagnóstico Médico', 'Ana Sofía Gómez', 3, 2024, 'Universidad Local', NULL, 2, 2, 'T-01'),
('Optimización de Cadenas de Suministro', 'Luis Fernando Torres', 3, 2023, 'Universidad Local', NULL, 2, 2, 'T-02'),

-- CDs (id_tipo = 4)
('Curso de Inglés Interactivo B1', 'Audio Learning', 4, 2020, 'Idiomas Inc.', NULL, 20, 20, 'CD-01'),
('Sinfonías de Beethoven (Colección Completa)', 'Beethoven', 4, 1999, 'Deutsche Grammophon', NULL, 5, 5, 'CD-02'),

-- Documentos (id_tipo = 5)
('Manual de Convivencia de la Biblioteca', 'Biblioteca Admin', 5, 2022, 'Biblioteca', NULL, 1, 1, 'D-01'),
('Mapa Histórico de la Ciudad (Réplica)', 'Archivo Municipal', 5, 1980, 'Gobierno Local', NULL, 3, 3, 'D-02');

                                                                                                                           ('Orgullo y Prejuicio', 'Jane Austen', 1, 1813, 'T. Egerton', '978-0199535569', 4, 4, 'CL-A-02'),
                                                                                                                                              ('Fundamentos de Bases de Datos', 'Silberschatz, Korth, Sudarshan', 1, 2019, 'McGraw-Hill', '978-0078022159', 6, 6, 'CI-C-14');

-- ============================================================================
-- Verificación
-- ============================================================================
SELECT * FROM documentos;

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
