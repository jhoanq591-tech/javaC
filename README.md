# CrediYa S.A.S. (consola Java + MySQL)

Sistema de consola en Java 17 (Maven) para gestionar empleados, clientes, préstamos y pagos con persistencia en MySQL mediante JDBC.

## Requisitos
- JDK 17 o superior y Maven (NetBeans los trae)
- MySQL 8 encendido (puerto 3306)
- Internet la primera vez (Maven descarga el driver y JUnit)

## Puesta en marcha

### 1. Crear la base de datos
```
mysql -u root -p < sql/schema.sql
```
Crea `crediya_db`, las 4 tablas (con restricciones `NOT NULL`, `CHECK`, llaves foráneas e índices) y datos de ejemplo. Cada ejecución borra y recrea la base.

### 2. Configurar la conexión (sin credenciales en el código)
La conexión se lee de variables de entorno. Si no existen, se usa `jdbc:mysql://localhost:3306/crediya_db` con el usuario `root` y clave vacía (solo para desarrollo local).

| Variable | Significado |
|---|---|
| `DB_URL` | URL JDBC, por ejemplo `jdbc:mysql://localhost:3306/crediya_db` |
| `DB_USER` | usuario de MySQL |
| `DB_PASSWORD` | clave de MySQL |

En producción conviene un usuario con permisos mínimos en lugar de `root`:
```sql
CREATE USER 'crediya_app'@'localhost' IDENTIFIED BY '<una clave propia>';
GRANT SELECT, INSERT, UPDATE ON crediya_db.* TO 'crediya_app'@'localhost';
```

### 3. Ejecutar
```
mvn test
mvn compile exec:java
```
En NetBeans: abrir la carpeta con el `pom.xml` y ejecutar `crediya.Main`.

## Usuarios de ejemplo (solo demostración)
| Rol | Quién | Documento | Clave |
|---|---|---|---|
| Administrador | Laura Gomez | 1001 | admin123 |
| Cliente | Carlos Ruiz | 2001 | carlos123 |
| Cliente | Ana Torres | 2002 | ana123 |
| Cliente | Pedro Castro | 2003 | pedro123 |

Las claves nuevas exigen entre 8 y 64 caracteres. Las de ejemplo son débiles a propósito: cámbielas en un uso real.

## Datos de ejemplo
3 empleados, 3 clientes, 7 préstamos y 3 pagos.
- Activos: 1, 2, 6 y 7
- Vencidos por fecha: 2, 3 y 7
- Préstamo 4: pendiente. Préstamo 5: cancelado. Préstamo 3: pagado.

## Reglas de negocio
- Estados: `PENDIENTE`, `ACTIVO` (aprobado por el administrador), `CANCELADO`, `PAGADO`.
- Un préstamo nuevo queda `PENDIENTE`. Al aprobarlo pasa a `ACTIVO` y la fecha de vencimiento es la fecha de hoy más el número de cuotas en meses.
- Interés fijo del 5 %: total = monto + monto × interés / 100; cuota = total / cuotas.
- Un cliente no puede pedir más de 5 veces sus ingresos; las cuotas van de 1 a 120.
- Solo se abonan préstamos `ACTIVO`; el abono no puede superar el saldo; al llegar el saldo a cero el préstamo pasa a `PAGADO` (en una sola transacción).
- Moroso = `ACTIVO` con más de 30 días desde su inicio.

## Etapa 2: GestorPrestamos
Clase `crediya.servicio.GestorPrestamos`, sobre el modelo `crediya.modelo.Prestamo` (id, cliente_id, monto, interes, fecha_prestamo, fecha_vencimiento, estado).

| Método | Qué hace |
|---|---|
| `cargarPrestamos()` | Lee la tabla `prestamos` con JDBC y devuelve `List<Prestamo>` |
| `filtrarPrestamosVencidos(lista)` | Stream + lambda: fecha de vencimiento anterior a hoy |
| `filtrarPrestamosActivos(lista)` | Stream + lambda: estado `ACTIVO` |
| `mostrarResumen(lista)` | Imprime activos, vencidos y monto total adeudado |

- El monto adeudado es el saldo pendiente (monto + interés − abonos) de los préstamos activos.
- Vencido se decide solo por la fecha (por eso el préstamo 3, `PAGADO`, aparece como vencido).
- Cada método usa `try-catch` y muestra mensajes claros: error de conexión, error de datos, tabla sin datos o lista vacía/nula.
- Cómo usarlo: iniciar sesión como administrador → `5. Reportes` → `3. Gestor de préstamos`.

### Capturas de consola
- `docs/consola-resultados.txt`: filtrado y resumen.
- `docs/consola-error-conexion.txt`: caso de error de conexión.

```
=========== RESUMEN DE PRESTAMOS ===========
Total de prestamos:   7
Prestamos activos:    4
Prestamos vencidos:   3
Monto total adeudado: $5.135.000,00
============================================
```

## Arquitectura y principios SOLID
```
crediya
├── Main                      raíz de composición: crea y conecta todo
├── configuracion             ProveedorConexion (interfaz), ConexionMySql, ConfiguracionBaseDatos
├── modelo                    Prestamo, Cliente, Empleado, Pago, Credencial, EstadoPrestamo, Rol
├── repositorio               interfaces de acceso a datos
│   └── jdbc                  implementaciones JDBC (consultas parametrizadas)
├── servicio                  reglas de negocio, GestorPrestamos, validaciones
├── seguridad                 HasheadorClaves (interfaz) y HasheadorPbkdf2
├── consola                   menús y entrada/salida de usuario
├── excepcion                 ErrorDeNegocio, ErrorDeDatos, ErrorDeConexion
└── util                      Formato
```
- **S (responsabilidad única):** cada clase hace una sola cosa (repositorio = SQL, servicio = reglas, consola = interacción).
- **O (abierto/cerrado):** `Menu` recibe opciones; agregar una opción no modifica el ciclo del menú.
- **L (Liskov):** cualquier implementación de un repositorio sirve donde se pide su interfaz (las pruebas usan versiones en memoria).
- **I (segregación de interfaces):** interfaces pequeñas por tema (`RepositorioCredenciales` aparte de `RepositorioClientes`).
- **D (inversión de dependencias):** los servicios dependen de interfaces, no de JDBC; las dependencias y el `Clock` se inyectan desde `Main`.

## Seguridad y buenas prácticas
- Todas las consultas usan `PreparedStatement` (sin concatenar texto: protegido contra inyección SQL).
- Claves con PBKDF2-HMAC-SHA256, 600 000 iteraciones y sal aleatoria por clave; comparación en tiempo constante; nunca se guardan ni se muestran en texto plano.
- Login con mensaje genérico, verificación de hash incluso si el usuario no existe y bloqueo de 1 minuto tras 5 intentos fallidos.
- La clave se lee sin eco cuando hay terminal real y se borra de memoria después de usarla.
- Los errores de base de datos no muestran detalles internos al usuario; el detalle técnico (sin credenciales) va al registro.
- Dinero con `BigDecimal` (nunca `double`), validación de todos los datos de entrada y restricciones `CHECK` en la base.
- Abono y cambio de estado a `PAGADO` en una transacción con `commit`/`rollback`.
- Credenciales de la base fuera del código (variables de entorno). Código sin comentarios y con pruebas unitarias (`mvn test`).
