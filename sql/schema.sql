-- ============================================================
-- CrediYa S.A.S. - Base de datos MySQL
-- Ejecutar completo (borra y vuelve a crear crediya_db):
--   mysql -u TU_USUARIO -p < sql/schema.sql
-- ============================================================
--
-- DIAGRAMA (relaciones)
--
--   empleados 1 ----< prestamos >---- 1 clientes
--                        |
--                        | 1
--                        |
--                        ^ N
--                      pagos
--
--   empleados(id, nombre, documento, rol, correo, salario, clave)
--   clientes (id, nombre, documento, correo, telefono, clave, ingresos)
--   prestamos(id, cliente_id -> clientes, empleado_id -> empleados,
--             monto, interes %, cuotas, fecha_prestamo, fecha_vencimiento, estado)
--   pagos    (id, prestamo_id -> prestamos, fecha_pago, monto)
--
--   ROLES:
--     Administrador = un empleado con rol 'Administrador'
--     Usuario       = un cliente
--
--   ESTADOS de un prestamo:
--     pendiente -> el cliente lo solicito y espera respuesta
--     ACTIVO    -> el administrador lo aprobo (se puede pagar)
--     cancelado -> el administrador lo rechazo
--     pagado    -> ya se pago todo
-- ============================================================

DROP DATABASE IF EXISTS crediya_db;
CREATE DATABASE crediya_db;
USE crediya_db;

CREATE TABLE empleados (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80),
  documento VARCHAR(30) UNIQUE,
  rol VARCHAR(30),
  correo VARCHAR(80),
  salario DECIMAL(10,2),
  clave VARCHAR(30)
);

CREATE TABLE clientes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80),
  documento VARCHAR(30) UNIQUE,
  correo VARCHAR(80),
  telefono VARCHAR(20),
  clave VARCHAR(30),
  ingresos DECIMAL(12,2)
);

CREATE TABLE prestamos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT,
  empleado_id INT,
  monto DECIMAL(12,2),
  interes DECIMAL(5,2),
  cuotas INT,
  fecha_prestamo DATE,
  fecha_vencimiento DATE,
  estado VARCHAR(20),
  FOREIGN KEY (cliente_id) REFERENCES clientes(id),
  FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

CREATE TABLE pagos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  prestamo_id INT,
  fecha_pago DATE,
  monto DECIMAL(10,2),
  FOREIGN KEY (prestamo_id) REFERENCES prestamos(id)
);

-- ============================================================
-- DATOS INVENTADOS (para probar desde la terminal)
-- Todos los documentos y claves son de ejemplo.
-- ============================================================

-- Administrador: documento 1001, clave admin123
INSERT INTO empleados (nombre, documento, rol, correo, salario, clave) VALUES
  ('Laura Gomez',  '1001', 'Administrador', 'laura@ejemplo.com',  3500000, 'admin123'),
  ('Mario Rojas',  '1002', 'Asesor',        'mario@ejemplo.com',  2200000, 'asesor123'),
  ('Sofia Perez',  '1003', 'Cobrador',      'sofia@ejemplo.com',  1800000, 'cobrador123');

-- Clientes: documento 2001 / carlos123, 2002 / ana123, 2003 / pedro123
INSERT INTO clientes (nombre, documento, correo, telefono, clave, ingresos) VALUES
  ('Carlos Ruiz',   '2001', 'carlos@ejemplo.com', '3001112222', 'carlos123', 2000000),
  ('Ana Torres',    '2002', 'ana@ejemplo.com',    '3003334444', 'ana123',    3000000),
  ('Pedro Castro',  '2003', 'pedro@ejemplo.com',  '3005556666', 'pedro123',  1500000);

-- Prestamo 1: ACTIVO, vence en el futuro
-- Prestamo 2: ACTIVO y VENCIDO (vencio hace 30 dias)
-- Prestamo 3: ya pagado (vencido en fecha, pero no cuenta como activo)
-- Prestamo 4: pendiente (esperando al administrador)
-- Prestamo 5: cancelado por el administrador
-- Prestamo 6: ACTIVO, vence en el futuro
-- Prestamo 7: ACTIVO y VENCIDO (vencio hace 5 dias)
INSERT INTO prestamos (cliente_id, empleado_id, monto, interes, cuotas, fecha_prestamo, fecha_vencimiento, estado) VALUES
  (1, 1, 1000000, 5, 10, CURDATE(),                            DATE_ADD(CURDATE(), INTERVAL 10 MONTH),            'ACTIVO'),
  (2, 1, 2000000, 4, 12, DATE_SUB(CURDATE(), INTERVAL 390 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY),             'ACTIVO'),
  (3, 1,  500000, 3,  5, DATE_SUB(CURDATE(), INTERVAL 240 DAY), DATE_SUB(CURDATE(), INTERVAL 90 DAY),             'pagado'),
  (1, NULL, 300000, 5, 3, CURDATE(),                           DATE_ADD(CURDATE(), INTERVAL 3 MONTH),             'pendiente'),
  (2, 1,  800000, 4,  4, DATE_SUB(CURDATE(), INTERVAL 20 DAY),  DATE_ADD(CURDATE(), INTERVAL 100 DAY),            'cancelado'),
  (3, 1,  600000, 5,  6, DATE_SUB(CURDATE(), INTERVAL 30 DAY),  DATE_ADD(CURDATE(), INTERVAL 150 DAY),            'ACTIVO'),
  (1, 1, 1500000, 5,  3, DATE_SUB(CURDATE(), INTERVAL 95 DAY),  DATE_SUB(CURDATE(), INTERVAL 5 DAY),             'ACTIVO');

INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES
  (2, DATE_SUB(CURDATE(), INTERVAL 30 DAY),  200000),
  (3, DATE_SUB(CURDATE(), INTERVAL 60 DAY),  250000),
  (3, DATE_SUB(CURDATE(), INTERVAL 30 DAY),  265000);
