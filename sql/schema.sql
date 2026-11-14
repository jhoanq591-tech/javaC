DROP DATABASE IF EXISTS crediya_db;
CREATE DATABASE crediya_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE crediya_db;

CREATE TABLE empleados (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL,
  documento VARCHAR(30) NOT NULL UNIQUE,
  rol VARCHAR(20) NOT NULL,
  correo VARCHAR(80) NOT NULL,
  salario DECIMAL(12,2) NOT NULL,
  clave_hash VARCHAR(255) NOT NULL,
  CONSTRAINT chk_empleados_rol CHECK (rol IN ('ADMINISTRADOR', 'ASESOR', 'COBRADOR')),
  CONSTRAINT chk_empleados_salario CHECK (salario > 0)
) ENGINE=InnoDB;

CREATE TABLE clientes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL,
  documento VARCHAR(30) NOT NULL UNIQUE,
  correo VARCHAR(80) NOT NULL,
  telefono VARCHAR(20) NOT NULL,
  ingresos DECIMAL(14,2) NOT NULL,
  clave_hash VARCHAR(255) NOT NULL,
  CONSTRAINT chk_clientes_ingresos CHECK (ingresos > 0)
) ENGINE=InnoDB;

CREATE TABLE prestamos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT NOT NULL,
  empleado_id INT NULL,
  monto DECIMAL(14,2) NOT NULL,
  interes DECIMAL(5,2) NOT NULL,
  cuotas INT NOT NULL,
  fecha_prestamo DATE NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  estado VARCHAR(20) NOT NULL,
  CONSTRAINT fk_prestamos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
  CONSTRAINT fk_prestamos_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id),
  CONSTRAINT chk_prestamos_monto CHECK (monto > 0),
  CONSTRAINT chk_prestamos_interes CHECK (interes BETWEEN 0 AND 100),
  CONSTRAINT chk_prestamos_cuotas CHECK (cuotas BETWEEN 1 AND 120),
  CONSTRAINT chk_prestamos_estado CHECK (estado IN ('PENDIENTE', 'ACTIVO', 'CANCELADO', 'PAGADO')),
  INDEX idx_prestamos_cliente (cliente_id),
  INDEX idx_prestamos_estado (estado),
  INDEX idx_prestamos_vencimiento (fecha_vencimiento)
) ENGINE=InnoDB;

CREATE TABLE pagos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  prestamo_id INT NOT NULL,
  fecha_pago DATE NOT NULL,
  monto DECIMAL(14,2) NOT NULL,
  CONSTRAINT fk_pagos_prestamo FOREIGN KEY (prestamo_id) REFERENCES prestamos(id),
  CONSTRAINT chk_pagos_monto CHECK (monto > 0),
  INDEX idx_pagos_prestamo (prestamo_id)
) ENGINE=InnoDB;

INSERT INTO empleados (nombre, documento, rol, correo, salario, clave_hash) VALUES
  ('Laura Gomez', '1001', 'ADMINISTRADOR', 'laura@ejemplo.com', 3500000, 'pbkdf2_sha256$600000$zdEKOK3DDt3V1gjZnf+yOA==$/dG7gsYb5lLS3/fAV44eAQfxXjmKa8UD7HbOyjJoZTk='),
  ('Mario Rojas', '1002', 'ASESOR', 'mario@ejemplo.com', 2200000, 'pbkdf2_sha256$600000$s6GM/GGg4/WYQAog+blakQ==$P2VT2ncju63yj7INruiE7rE0VQytin+IIn3uz5vOYqA='),
  ('Sofia Perez', '1003', 'COBRADOR', 'sofia@ejemplo.com', 1800000, 'pbkdf2_sha256$600000$kfkU5U2HtUoHslmtitSn1A==$18eK3V35IXeUac3F6o4PmfS5r6J+v1+vodo7lz99cQw=');

INSERT INTO clientes (nombre, documento, correo, telefono, ingresos, clave_hash) VALUES
  ('Carlos Ruiz', '2001', 'carlos@ejemplo.com', '3001112222', 2000000, 'pbkdf2_sha256$600000$ChfiHh8PHmtFweTQzqxr7g==$ShB3NMfYG5l9fWrLkpT8AxuhVjj8MTZrXojLuZaoFsk='),
  ('Ana Torres', '2002', 'ana@ejemplo.com', '3003334444', 3000000, 'pbkdf2_sha256$600000$2B3nBH7O+GzfVZnbnaNjkA==$BBEbW8hSQqrp4qmhliIivlBnbXL2ivb2q45Fls7n/5o='),
  ('Pedro Castro', '2003', 'pedro@ejemplo.com', '3005556666', 1500000, 'pbkdf2_sha256$600000$5cTDWjMYDYjSrR7ds2vR+A==$3ohFrdTAGNY2Lby0b5n6+ZKHzqPHrei9DMm/j2/gC8s=');

INSERT INTO prestamos (cliente_id, empleado_id, monto, interes, cuotas, fecha_prestamo, fecha_vencimiento, estado) VALUES
  (1, 1, 1000000, 5, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 MONTH), 'ACTIVO'),
  (2, 1, 2000000, 4, 12, DATE_SUB(CURDATE(), INTERVAL 390 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'ACTIVO'),
  (3, 1, 500000, 3, 5, DATE_SUB(CURDATE(), INTERVAL 240 DAY), DATE_SUB(CURDATE(), INTERVAL 90 DAY), 'PAGADO'),
  (1, NULL, 300000, 5, 3, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 MONTH), 'PENDIENTE'),
  (2, 1, 800000, 4, 4, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_ADD(CURDATE(), INTERVAL 100 DAY), 'CANCELADO'),
  (3, 1, 600000, 5, 6, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 150 DAY), 'ACTIVO'),
  (1, 1, 1500000, 5, 3, DATE_SUB(CURDATE(), INTERVAL 95 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'ACTIVO');

INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES
  (2, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 200000),
  (3, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 250000),
  (3, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 265000);
