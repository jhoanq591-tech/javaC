# CrediYa S.A.S. (consola Java + MySQL)

Programa de consola en Java (Maven) que guarda empleados, clientes, préstamos y pagos en MySQL.

## Requisitos
- NetBeans con Java 17 o superior (trae Maven incluido)
- MySQL o MariaDB encendido (puerto 3306)
- Internet la primera vez (Maven descarga el driver de MySQL)

## Pasos

### 1. Crear la base de datos
Ejecuta el script `sql/schema.sql` en tu MySQL (Workbench, DBeaver, phpMyAdmin o terminal).
Crea la base `crediya_db`, las 4 tablas y datos de ejemplo.

Desde la terminal:

```
mysql -u root -p < sql/schema.sql
```

### 2. Revisar usuario y contraseña
Abre `src/main/java/crediya/Conexion.java`. Arriba hay 3 líneas:

```java
private static final String URL = "jdbc:mysql://localhost:3306/crediya_db";
private static final String USUARIO = "root";
private static final String CLAVE = "";
```

Si tu MySQL usa otro usuario o contraseña, cámbialos ahí.

### 3. Abrir y ejecutar en NetBeans
1. `File > Open Project` y elige la carpeta `crediya-java` (la que tiene el `pom.xml`).
2. Espera a que Maven descargue las dependencias.
3. Clic derecho sobre `Main.java` (en `src/main/java/crediya`) y `Run File`, o presiona `Shift + F6`.
4. Usa el menú en la consola de salida de NetBeans.

## Roles y usuarios de ejemplo
Al iniciar el programa se elige cómo entrar, con documento y clave:

| Rol | Quién es | Documento | Clave |
|---|---|---|---|
| Administrador | Laura Gomez (empleada con rol Administrador) | 1001 | admin123 |
| Usuario (cliente) | Carlos Ruiz | 2001 | carlos123 |
| Usuario (cliente) | Ana Torres | 2002 | ana123 |
| Usuario (cliente) | Pedro Castro | 2003 | pedro123 |

- **Cliente:** solicita préstamos y ve solo los suyos, con su estado, lo pagado y el saldo.
- **Administrador:** ve los préstamos de todos los clientes (separados por cliente), aprueba, cancela o deja pendiente cada préstamo, y gestiona empleados, clientes, pagos y reportes.

## Datos de ejemplo
- 3 empleados, 3 clientes, 7 préstamos y 3 pagos.
- Activos: 1, 2, 6 y 7. Vencidos (por fecha): 2, 3 y 7. Préstamo 4: pendiente. Préstamo 5: cancelado.

Nota: `sql/schema.sql` borra y vuelve a crear `crediya_db` cada vez que se ejecuta.

## Scripts SQL
Son los mismos que están en `sql/schema.sql`. Se pueden copiar y ejecutar en este orden.

### Script 1: crear la base de datos y las tablas

```sql
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
```

### Script 2: insertar los datos de ejemplo

```sql
USE crediya_db;

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
```

## Reglas
- Estados de un préstamo: `pendiente` (solicitado), `ACTIVO` (aprobado por el administrador), `cancelado` y `pagado`.
- Un cliente no puede pedir más de 5 veces sus ingresos.
- Interés total fijo del 5 %: total = monto + monto*interes/100, cuota = total/cuotas.
- Solo se pueden abonar préstamos `ACTIVO`. Moroso = ACTIVO con más de 30 días.
- Las claves se guardan como texto simple porque es un proyecto de práctica.

## Etapa 2: GestorPrestamos (préstamos activos y vencidos)

La tabla `prestamos` ahora tiene `fecha_prestamo` y `fecha_vencimiento` (antes `fecha_inicio`), y el estado de un préstamo aprobado es `ACTIVO`. La fecha de vencimiento = fecha del préstamo + número de cuotas en meses.

Clases nuevas:
- `Prestamo.java`: objeto con id, cliente_id, monto, interes, fechaPrestamo, fechaVencimiento y estado.
- `GestorPrestamos.java`:
  - `cargarPrestamos()`: lee la tabla con JDBC y devuelve `List<Prestamo>`.
  - `filtrarPrestamosVencidos(lista)`: Stream + lambda, fecha de vencimiento anterior a hoy.
  - `filtrarPrestamosActivos(lista)`: Stream + lambda, estado `ACTIVO`.
  - `mostrarResumen(lista)`: imprime activos, vencidos y monto total adeudado (saldo pendiente de los activos: monto + interés - abonos).
  - Todos los métodos usan try-catch con mensajes claros (error de conexión, lista vacía, etc.).

Cómo usarlo: entrar como administrador, `5. Reportes`, `3. Gestor de préstamos`.

Notas: "vencido" se decide solo por la fecha (por eso el préstamo 3, `pagado`, aparece como vencido); el monto adeudado suma solo los activos.

### Capturas de consola
Salida real (texto) en `docs/`:
- `docs/consola-resultados.txt`: lista, activos, vencidos, resumen y caso de lista vacía.
- `docs/consola-error-conexion.txt`: caso de error de conexión.

```
=========== RESUMEN DE PRESTAMOS ===========
Total de prestamos:        7
Prestamos activos:         4
Prestamos vencidos:        3
Monto total adeudado:      $5.135.000,00
============================================
```
