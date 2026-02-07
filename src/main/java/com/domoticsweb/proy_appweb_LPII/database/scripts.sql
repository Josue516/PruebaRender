
DROP DATABASE IF EXISTS `Domotics_DB`;

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

-- 4) USUARIOS_ROLES
CREATE TABLE usuarios_roles (
  idUsuario BIGINT UNSIGNED NOT NULL,
  idRol BIGINT UNSIGNED NOT NULL,
  fechaAsignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (idUsuario, idRol),
  KEY ix_usuarios_roles_idRol (idRol),

  CONSTRAINT fk_ur_usuario
    FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT fk_ur_rol
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

-- =========================================================
-- 5) CATEGORIAS
-- =========================================================
CREATE TABLE categorias (
  idCategoria BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(200) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (idCategoria),
  UNIQUE KEY uk_categorias_nombre (nombre)
) ENGINE=InnoDB;


-- =========================================================
-- 6) PRODUCTOS
-- =========================================================
CREATE TABLE productos (
  idProducto BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(120) NOT NULL,
  marca VARCHAR (50),
  descripcion TEXT NULL,
  precio DECIMAL(10,2) NOT NULL,

  idCategoria BIGINT NOT NULL,

  activo BOOLEAN NOT NULL DEFAULT TRUE,
  fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (idProducto),
  KEY ix_productos_categoria (idCategoria),

  CONSTRAINT chk_productos_precio
    CHECK (precio >= 0),

  CONSTRAINT fk_productos_categoria
    FOREIGN KEY (idCategoria) REFERENCES categorias(idCategoria)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;



-- =========================================================
-- 7) INVENTARIO
-- =========================================================
CREATE TABLE inventario (
  idInventario BIGINT NOT NULL AUTO_INCREMENT,
  idProducto BIGINT NOT NULL,

  stock INT NOT NULL DEFAULT 0,
  stockMinimo INT NOT NULL DEFAULT 0,

  fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (idInventario),
  UNIQUE KEY uk_inventario_producto (idProducto),

  CONSTRAINT chk_inventario_stock
    CHECK (stock >= 0),

  CONSTRAINT chk_inventario_stockMinimo
    CHECK (stockMinimo >= 0),

  CONSTRAINT fk_inventario_producto
    FOREIGN KEY (idProducto) REFERENCES productos(idProducto)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 8) PRODUCTOS_IMAGENES
-- =========================================================
CREATE TABLE producto_imagenes (
  idImagen BIGINT NOT NULL AUTO_INCREMENT,
  idProducto BIGINT NOT NULL,

  urlImagen VARCHAR(500) NOT NULL,
  orden INT NOT NULL DEFAULT 1,
  activo TINYINT(1) NOT NULL DEFAULT 1,

  fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (idImagen),
  KEY ix_imagenes_producto (idProducto),

  CONSTRAINT fk_imagenes_producto
    FOREIGN KEY (idProducto) REFERENCES productos(idProducto)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;