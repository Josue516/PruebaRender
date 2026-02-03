
DROP DATABASE IF EXISTS `Domotics DB`;

-- 2) Crear base de datos nuevamente
CREATE DATABASE `Domotics DB`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `Domotics DB`;

-- =========================================================
-- 1) ROLES
-- =========================================================
CREATE TABLE roles (
  idRol BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  descripcion VARCHAR(200) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (idRol),
  UNIQUE KEY uk_roles_nombre (nombre)
) ENGINE=InnoDB;

-- =========================================================
-- 2) USUARIOS (Login)
-- =========================================================
CREATE TABLE usuarios (
  idUsuario BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

  nombreUsuario VARCHAR(50) NULL,
  correo VARCHAR(120) NOT NULL,
  contrasenaHash VARCHAR(255) NOT NULL,

  activo TINYINT(1) NOT NULL DEFAULT 1,
  correoVerificadoEn DATETIME NULL,
  ultimoLoginEn DATETIME NULL,
  intentosFallidos INT NOT NULL DEFAULT 0,
  bloqueadoHasta DATETIME NULL,

  fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (idUsuario),
  UNIQUE KEY uk_usuarios_correo (correo),
  UNIQUE KEY uk_usuarios_nombreUsuario (nombreUsuario),
  KEY ix_usuarios_activo (activo)
) ENGINE=InnoDB;

-- =========================================================
-- 3) DATOS PERSONALES (Perfil)
-- =========================================================
CREATE TABLE datos_personales (
  idUsuario BIGINT UNSIGNED NOT NULL,

  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  celular VARCHAR(20) NULL,

  tipoDocumento VARCHAR(20) NULL,
  numeroDocumento VARCHAR(30) NULL,
  fechaNacimiento DATE NULL,

  direccionLinea1 VARCHAR(120) NULL,
  direccionLinea2 VARCHAR(120) NULL,
  ciudad VARCHAR(60) NULL,
  region VARCHAR(60) NULL,
  pais VARCHAR(60) NULL,
  codigoPostal VARCHAR(15) NULL,

  fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (idUsuario),
  UNIQUE KEY uk_datos_documento (tipoDocumento, numeroDocumento),

  CONSTRAINT fk_datos_personales_usuario
    FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- 4) USUARIOS_ROLES (Muchos a muchos)
-- =========================================================
CREATE TABLE usuarios_roles (
  idUsuario BIGINT UNSIGNED NOT NULL,
  idRol BIGINT UNSIGNED NOT NULL,
  fechaAsignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (idUsuario, idRol),
  KEY ix_usuarios_roles_idRol (idRol),

  CONSTRAINT fk_usuarios_roles_usuario
    FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT fk_usuarios_roles_rol
    FOREIGN KEY (idRol) REFERENCES roles(idRol)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- DATA INICIAL: ROLES BASE
-- =========================================================
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Acceso total al sistema'),
('USUARIO', 'Usuario estándar');

-- =========================================================
-- EJEMPLO: Usuario admin (hash DEMO)
-- =========================================================
INSERT INTO usuarios (nombreUsuario, correo, contrasenaHash, activo)
VALUES ('admin', 'admin@demo.com', '$2b$12$REEMPLAZA_POR_HASH_REAL', 1);

INSERT INTO datos_personales (idUsuario, nombres, apellidos, celular)
VALUES (LAST_INSERT_ID(), 'Admin', 'Principal', '999999999');

INSERT INTO usuarios_roles (idUsuario, idRol)
SELECT u.idUsuario, r.idRol
FROM usuarios u
JOIN roles r ON r.nombre = 'ADMIN'
WHERE u.correo = 'admin@demo.com';
