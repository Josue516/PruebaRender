-- =========================================================
-- RESET TOTAL
-- =========================================================
DROP DATABASE IF EXISTS `Domotics_DB`;

CREATE DATABASE `Domotics_DB`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `Domotics_DB`;

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
-- 2) USUARIOS
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
-- 3) DATOS PERSONALES
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
-- 4) USUARIOS_ROLES
-- =========================================================
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
-- 5) CATEGORIAS
-- =========================================================
CREATE TABLE categorias (
  idCategoria BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
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
  idProducto BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(120) NOT NULL,
  marca VARCHAR(50) NULL,
  descripcion TEXT NULL,
  precio DECIMAL(10,2) NOT NULL,
  idCategoria BIGINT UNSIGNED NOT NULL,

  activo TINYINT(1) NOT NULL DEFAULT 1,
  fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (idProducto),
  KEY ix_productos_categoria (idCategoria),

  CONSTRAINT chk_productos_precio CHECK (precio >= 0),

  CONSTRAINT fk_productos_categoria
    FOREIGN KEY (idCategoria) REFERENCES categorias(idCategoria)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- 7) INVENTARIO
-- =========================================================
CREATE TABLE inventario (
  idInventario BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  idProducto BIGINT UNSIGNED NOT NULL,

  stock INT NOT NULL DEFAULT 0,
  stockMinimo INT NOT NULL DEFAULT 0,

  fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (idInventario),
  UNIQUE KEY uk_inventario_producto (idProducto),

  CONSTRAINT chk_inventario_stock CHECK (stock >= 0),
  CONSTRAINT chk_inventario_stockMinimo CHECK (stockMinimo >= 0),

  CONSTRAINT fk_inventario_producto
    FOREIGN KEY (idProducto) REFERENCES productos(idProducto)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- 8) PRODUCTO_IMAGENES
-- =========================================================
CREATE TABLE producto_imagenes (
  idImagen BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  idProducto BIGINT UNSIGNED NOT NULL,

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

-- =========================================================
-- 9) VENTAS (carrito/checkout)
-- =========================================================
CREATE TABLE ventas (
  idVenta BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  idUsuario BIGINT UNSIGNED NULL,
  fechaVenta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total DECIMAL(10,2) NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'PAGADA',

  PRIMARY KEY (idVenta),
  KEY ix_ventas_usuario (idUsuario),
  KEY ix_ventas_fecha (fechaVenta),

  CONSTRAINT fk_ventas_usuario
    FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- 10) DETALLE_VENTA
-- =========================================================
CREATE TABLE detalle_venta (
  idDetalle BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  idVenta BIGINT UNSIGNED NOT NULL,
  idProducto BIGINT UNSIGNED NOT NULL,
  cantidad INT NOT NULL,
  precioUnitario DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,

  PRIMARY KEY (idDetalle),
  KEY ix_detalle_venta (idVenta),
  KEY ix_detalle_producto (idProducto),

  CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
  CONSTRAINT chk_detalle_precio CHECK (precioUnitario >= 0),
  CONSTRAINT chk_detalle_subtotal CHECK (subtotal >= 0),

  CONSTRAINT fk_detalle_venta
    FOREIGN KEY (idVenta) REFERENCES ventas(idVenta)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT fk_detalle_producto
    FOREIGN KEY (idProducto) REFERENCES productos(idProducto)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- DATOS BASE (ROLES + ADMINS)
-- =========================================================
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Acceso total al sistema'),
('USUARIO', 'Usuario estándar');

-- BCrypt para "admin123"
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO usuarios (nombreUsuario, correo, contrasenaHash, activo) VALUES
('gerald', 'gerald@ieodomotics.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1),
('mary',   'mary@ieodomotics.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1);

INSERT INTO datos_personales (idUsuario, nombres, apellidos, celular)
SELECT idUsuario, 'Gerald', 'Admin', '999111222'
FROM usuarios WHERE nombreUsuario = 'gerald';

INSERT INTO datos_personales (idUsuario, nombres, apellidos, celular)
SELECT idUsuario, 'Mary', 'Admin', '999333444'
FROM usuarios WHERE nombreUsuario = 'mary';

INSERT INTO usuarios_roles (idUsuario, idRol)
SELECT u.idUsuario, r.idRol
FROM usuarios u
JOIN roles r ON r.nombre = 'ADMIN'
WHERE u.nombreUsuario IN ('gerald','mary');

-- =========================================================
-- SEMILLA DASHBOARD (categorías, productos, inventario, ventas)
-- =========================================================
INSERT INTO categorias (nombre, descripcion) VALUES
('Cámaras', 'Cámaras de seguridad'),
('Sensores', 'Sensores inteligentes'),
('Accesos', 'Cerraduras y control de acceso'),
('Energía', 'Enchufes y energía inteligente');

INSERT INTO productos (nombre, marca, descripcion, precio, idCategoria) VALUES
('Kit Cámara WiFi', 'IEO', 'Cámara 1080p con app móvil', 249.90, 1),
('Sensor de Movimiento', 'IEO', 'Sensor PIR inteligente', 79.90, 2),
('Cerradura Inteligente', 'IEO', 'Huella + PIN + App', 399.90, 3),
('Enchufe Smart', 'IEO', 'Control remoto desde app', 59.90, 4),
('Videoportero IP', 'IEO', 'Video HD con visión nocturna', 499.90, 1);

INSERT INTO inventario (idProducto, stock, stockMinimo) VALUES
(1, 8, 5),
(2, 3, 5),
(3, 6, 3),
(4, 2, 4),
(5, 1, 2);

INSERT INTO ventas (idUsuario, total, estado) VALUES
(NULL, 650.00, 'PAGADA'),
(NULL, 450.00, 'PAGADA'),
(NULL, 700.00, 'PAGADA'),
(NULL, 300.00, 'PAGADA');

INSERT INTO detalle_venta (idVenta, idProducto, cantidad, precioUnitario, subtotal) VALUES
(1, 1, 2, 249.90, 499.80),
(1, 2, 1, 79.90, 79.90),
(2, 3, 1, 399.90, 399.90),
(2, 4, 1, 59.90, 59.90),
(3, 1, 1, 249.90, 249.90),
(3, 5, 1, 499.90, 499.90),
(4, 2, 2, 79.90, 159.80);

