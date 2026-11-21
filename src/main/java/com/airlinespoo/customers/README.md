# Módulo 1 · Clientes y catálogos

Rama `feature/modulo-1-clientes` · Paquete `com.airlinespoo.customers` · Tablas `tblCountries`,
`tblDocumentsType`, `tblCustomers`.

Este módulo gestiona los clientes de la aerolínea y los dos catálogos que los describen: el país
al que pertenecen y el tipo de documento con el que se identifican. El Módulo 5 (reservas) lo usa
para saber quién reserva.

Aquí quedan registradas las clases que entrega el módulo, las reglas que valida y las decisiones
que se tomaron, como pide la guía ("Tus decisiones están anotadas en el README"). El README
general del proyecto lo mantiene el Módulo 3.

## Clases

| Capa | Clase | Qué hace |
| --- | --- | --- |
| `model` | `Country` | País: id numérico que escribe el usuario (1 a 195) y nombre (máx. 20). |
| `model` | `DocumentType` | Tipo de documento: código de 2 caracteres en mayúsculas y descripción (máx. 20). |
| `model` | `Customer` | Cliente: documento como id, nombres, apellidos, teléfono, correo y dirección opcionales, y **objetos** `DocumentType` y `Country`. |
| `repository` | `CountryRepository` | CRUD en memoria de países (hereda del kit). |
| `repository` | `DocumentTypeRepository` | CRUD en memoria de tipos de documento (hereda del kit). |
| `repository` | `CustomerRepository` | Interfaz: el CRUD del kit más las búsquedas de clientes (por correo, apellido, país y tipo). |
| `repository` | `InMemoryCustomerRepository` | Implementación en memoria de `CustomerRepository`. |
| `repository` | `JdbcDatabaseConnection` | Abre la conexión JDBC a SQL Server (URL, usuario y contraseña). |
| `repository` | `JdbcCountryRepository`, `JdbcDocumentTypeRepository` | CRUD con JDBC de países y tipos de documento; cumplen el contrato `Repository` del kit. |
| `repository` | `JdbcCustomerRepository` | Implementación con JDBC de `CustomerRepository`; arma cada cliente con un `INNER JOIN`. |
| `service` | `CustomerService` | Reglas de negocio de las tres entidades; equivale a `uspCrearClientes` con las reglas de la guía. |
| `service` | `CountryNameComparator`, `CustomerNameComparator` | Orden de los listados: países por nombre; clientes por apellido y luego nombre. |
| `ui` | `CustomerMenu` | Menú del módulo: registrar, consultar, actualizar contacto, listar, buscar, eliminar, catálogos, volver. |
| `ui` | `CustomerSampleData` | Carga 5 países, 4 tipos de documento y 3 clientes de ejemplo. |

## Cómo se relacionan

```text
CustomerMenu ──▶ CustomerService ──▶ CustomerRepository (interfaz) ◁── InMemoryCustomerRepository
                       │                      └──▶ Customer ──▶ DocumentType
                       │                                   └──▶ Country
                       ├──▶ Repository<Country, Integer>      ◁── CountryRepository
                       └──▶ Repository<DocumentType, String>  ◁── DocumentTypeRepository
```

- Regla de dependencia: `ui → service → repository → model`, nunca al revés ni saltando capas.
- El cliente **tiene** un tipo de documento y **pertenece** a un país: referencias a objetos, no
  códigos sueltos.
- El servicio recibe los repositorios por el constructor y depende de interfaces, no de las
  clases en memoria.

## Reglas que valida el modelo

| Clase | Campo | Regla | Dónde |
| --- | --- | --- | --- |
| `Country` | `id` | Obligatorio, entre 1 y 195 (total de países del mundo), no cambia. | Constructor |
| `Country` | `name` | Obligatorio, máximo 20 caracteres. | Constructor y `setName` |
| `DocumentType` | `id` | Obligatorio, exactamente 2 caracteres, se guarda en mayúsculas y sin espacios (`"cc"` → `"CC"`, `"C"` se rechaza). No cambia. | Constructor |
| `DocumentType` | `description` | Obligatoria, máximo 20 caracteres. | Constructor y `setDescription` |
| `Customer` | `id` (documento) | Obligatorio, máximo 20 caracteres, no cambia. | Constructor |
| `Customer` | `firstName`, `lastName` | Obligatorios, máximo 30 cada uno. | Constructor y setters |
| `Customer` | `documentType`, `country` | Obligatorios (no nulos). | Constructor y setters |
| `Customer` | `telephoneNumber` | Obligatorio, máximo 15, solo dígitos con `+` opcional al inicio. | Constructor, setter y `updateContact` |
| `Customer` | `email` | Opcional; si viene, máximo 80 y formato `algo@algo.algo`. | `setEmail` y `updateContact` |
| `Customer` | `address` | Opcional; si viene, máximo 40. | `setAddress` y `updateContact` |

Todas usan `Validator` del kit y lanzan `ValidationException` con el nombre del campo en el
mensaje. `equals` y `hashCode` se basan solo en el id. `toString` de `Customer` no muestra
teléfono, correo ni dirección.

## Reglas que valida el servicio · Mapa CRUD

| Entidad | Método del servicio | SQL equivalente | Regla |
| --- | --- | --- | --- |
| Country | `createCountry(id, name)` | `INSERT tblCountries` | Id no repetido (`DuplicateException`). |
| Country | `getCountry(id)` | `SELECT … WHERE id = ?` | No existe → `NotFoundException`. |
| Country | `listCountries()` | `SELECT … ORDER BY Name` | Ordenados por nombre. |
| Country | `updateCountry(id, name)` | `UPDATE … SET Name` | Nombre válido. |
| Country | `deleteCountry(id)` | `DELETE` | Rechazo (`BusinessRuleException`) si algún cliente es de ese país. |
| DocumentType | `createDocumentType(code, description)` | `INSERT tblDocumentsType` | Código normalizado y no repetido. |
| DocumentType | `getDocumentType(code)` | `SELECT … WHERE id = ?` | Normaliza el código antes de buscar; no existe → excepción. |
| DocumentType | `listDocumentTypes()` | `SELECT` | En orden de registro. |
| DocumentType | `updateDocumentType(code, description)` | `UPDATE … SET Description` | Descripción válida. |
| DocumentType | `deleteDocumentType(code)` | `DELETE` | Rechazo si algún cliente usa el tipo. |
| Customer | `createCustomer(id, firstName, lastName, documentTypeCode, telephoneNumber, email, countryId, address)` | `INSERT tblCustomers` (`uspCrearClientes`) | País y tipo existen; documento y correo no repetidos. |
| Customer | `getCustomer(id)` | `SELECT … WHERE customerId = ?` | No existe → excepción. |
| Customer | `listCustomers()` | `SELECT … ORDER BY Lastname, FirtName` | Ordenados por apellido y luego nombre. |
| Customer | `updateCustomer(id, telephoneNumber, email, address)` | `UPDATE tblCustomers SET …` | Solo datos de contacto; el correo no puede ser de **otro** cliente (el propio se acepta); se validan los tres datos antes de cambiar alguno. |
| Customer | `deleteCustomer(id)` | `DELETE` | Por ahora se permite; en la integración se impedirá si tiene reservas. |
| Customer | `getCustomerByEmail(email)` | `SELECT … WHERE Email = ?` | `Optional` vacío si no existe. |
| Customer | `searchCustomersByLastName(text)` | `SELECT … WHERE Lastname LIKE ?` | Coincidencia parcial sin distinguir mayúsculas. |
| Customer | `listCustomersByCountry(countryId)` | `SELECT … WHERE CountryId = ?` | El país debe existir. |
| Customer | `listCustomersByDocumentType(code)` | `SELECT … WHERE TypeId = ?` | El tipo debe existir. |
| Customer | `countCustomers()` | `SELECT COUNT(*)` | Total de clientes. |
| Customer | (interno) `existsByCountry`, `existsByDocumentType` | `SELECT 1 … WHERE …` | Impiden eliminar países y tipos en uso. |

## Decisiones

1. **`int` y no `short` para el id del país.** La columna es `SMALLINT`; la guía permite `int`
   si se justifica. Se usa `int`, que es el entero conocido, y el constructor limita el valor a
   1..195, que es el total de países del mundo y cabe en el rango de la columna.
2. **Los opcionales valen `null` cuando no se conocen.** Un texto vacío se convierte en `null` en
   un solo método privado (`optionalText`), que también controla la longitud.
3. **El código del tipo de documento se normaliza en el modelo** (`DocumentType.normalizeCode`):
   mayúsculas y sin espacios. El servicio usa el mismo método al buscar.
4. **El correo es único sin distinguir mayúsculas**: `Ana@mail.com` y `ana@mail.com` son el mismo
   buzón.
5. **`updateCustomer` cambia solo teléfono, correo y dirección**, como dice la tabla de
   operaciones de la guía ("Actualizar contacto"). El modelo tiene setters validados para los
   demás campos ("todo menos el documento"), por si el equipo amplía la operación. Los tres datos
   de contacto se validan antes de asignar (`Customer.updateContact`): si uno falla, el cliente
   queda como estaba.
6. **Orden con `Comparator` y `compareToIgnoreCase`.** Dos clases pequeñas (`CountryNameComparator`,
   `CustomerNameComparator`). Limitación conocida: un apellido que empieza con tilde (por ejemplo
   "Álvarez") queda después de la Z, porque `compareToIgnoreCase` compara por código de carácter.
   Por eso los datos de ejemplo no empiezan con tilde.
7. **El servicio depende de interfaces.** Para países y tipos de documento basta el contrato
   `Repository` del kit; para clientes se definió `CustomerRepository` (contrato con las
   búsquedas propias) y `InMemoryCustomerRepository` (implementación), igual que el kit separa
   `Repository` de `InMemoryRepository`. Por eso se pudo agregar JDBC sin tocar el servicio.
8. **Sin `IdGenerator`.** Ningún id del módulo es autogenerado.
9. **Un solo menú.** La guía pide una opción por módulo en el menú principal; los catálogos son un
   submenú dentro de `CustomerMenu`, necesario para ejecutar a mano los casos 1 a 3.
10. **Las listas que devuelve el servicio son copias**; modificarlas no altera el repositorio.
11. **Sintaxis sencilla a propósito**: ciclos `for`, condicionales, `Optional` del kit,
    `switch` con flechas (como en la lección G0 del curso). Sin lambdas ni streams. Las
    expresiones regulares (`String.matches`) y `Comparator` son los temas 🔎 que la guía pide
    investigar para este módulo.

## Datos de ejemplo

`CustomerSampleData.load(service)`: Colombia (1), Estados Unidos (2), México (3), España (4),
Argentina (5); CC, TI, CE, PA; Ana Pérez (CC 1001234567), Carlos Gómez (CC 98765432) y Lucía
Arango (PA AB123456). Los apellidos están desordenados a propósito para comprobar el orden del
listado. Pasan por el servicio, así se validan igual que los datos del usuario.

## Cómo compilar y ejecutar

```bash
mvn clean test
```

Mientras el Módulo 3 no entregue el `Main` definitivo, el módulo se arranca así (es lo que `Main`
hará en el orden "repositorios → servicios → datos de ejemplo → menús"):

```java
ConsoleInput input = new ConsoleInput();
CustomerService customerService = new CustomerService(
        new CountryRepository(), new DocumentTypeRepository(), new InMemoryCustomerRepository());
CustomerSampleData.load(customerService);
Menu customerMenu = new CustomerMenu(customerService, input);   // va en la lista de MainMenu
customerMenu.run();
```

Para probar a mano se puede pegar ese bloque temporalmente en `Main.main`, sin hacer commit de
`Main.java` (pertenece al Módulo 3).

## Persistencia con JDBC

Sigue la lección y el taller de JDBC del curso: `DriverManager`, `Connection`,
`PreparedStatement`, `ResultSet`, `try` con recursos e `INNER JOIN`. Solo cambió la capa de
repositorios; el modelo, el servicio y el menú son los mismos.

El motor es **SQL Server**, el del script de tablas del docente (`aerolines-poo_sqlserver.sql`).
Los nombres de tabla y de columna que usa el código son los de ese script.

**1. Crear la base de datos.** En SSMS, con una consulta nueva:

```sql
CREATE DATABASE airlinesdb;
```

**2. Crear las tablas con el script del docente.** Abrir `aerolines-poo_sqlserver.sql`, elegir
`airlinesdb` en la lista de bases de datos (arriba a la izquierda) y ejecutar con F5. El script no
elige la base de datos por sí mismo.

**3. Cargar los datos de ejemplo del módulo.** Ejecutar [`sql/sqlserver.sql`](sql/sqlserver.sql)
en esa misma base de datos. No crea tablas: solo inserta los 5 países, los 4 tipos de documento y
los 3 clientes, y solo si las tablas están vacías.

**4. Poner la contraseña en `JdbcDatabaseConnection`.** La del usuario `sa` del servidor local. La
URL es la de la lección de JDBC: `localhost:1433`, `encrypt=false` y `trustServerCertificate=true`.

**5. Arrancar con los repositorios JDBC.** Es el mismo bloque de arriba con otros repositorios.
No se llama a `CustomerSampleData.load`: los datos de ejemplo ya son filas de la base de datos y
cargarlos otra vez daría "ya existe".

```java
ConsoleInput input = new ConsoleInput();
CustomerService customerService = new CustomerService(
        new JdbcCountryRepository(), new JdbcDocumentTypeRepository(), new JdbcCustomerRepository());
Menu customerMenu = new CustomerMenu(customerService, input);
customerMenu.run();
```

### Decisiones de la capa JDBC

12. **Tablas y columnas del script del docente**: `tblCountries (CountryId, Name)`,
    `tblDocumentsType (TypeId, Description)` y `tblCustomers (customerId, FirtName, Lastname,
    TypeId, TelephoneNumber, Email, CountryId, Address)`. El módulo no crea tablas; solo aporta
    sus datos de ejemplo. `FirtName` conserva el error del esquema y en Java es `firstName`, como
    pide la guía (sección 5).
13. **Los ids no son autogenerados**: en el script del docente estas tres tablas no usan
    `IDENTITY`. El taller sí los autogenera porque sus tablas son distintas.
14. **Sin `ON DELETE CASCADE`**, igual que el script del docente. La clave foránea impide borrar un
    país o un tipo de documento en uso; el servicio lo rechaza antes con un mensaje claro.
15. **Los repositorios no imprimen.** Un `SQLException` se convierte en `AirlinesException`
    ("Error de base de datos: …") y el menú la muestra sin cerrarse, igual que las demás.
16. **Mismo contrato del kit.** `create` lanza `DuplicateException` si el id ya existe, `update`
    lanza `NotFoundException` si no modificó ninguna fila y `findById` devuelve `Optional`.
17. **Correo único.** El script del docente define `UNIQUE (Email)`. En SQL Server esa restricción
    admite un solo `NULL`: con ese esquema solo un cliente puede quedar sin correo, y el segundo
    recibe "Error de base de datos". No se cambió el esquema del docente.
18. **Una conexión por operación**, como en el taller. Es lo más sencillo para este tamaño.

El código compila y las consultas usan los nombres del script del docente. Falta ejecutarlo contra
SQL Server.

## Casos de prueba de la guía (ejecutados a mano por el menú)

| # | Acción | Resultado esperado | Cómo reproducirlo | Estado |
| --- | --- | --- | --- | --- |
| 1 | Crear país con nombre de 21 caracteres | Rechazo con mensaje claro | 7 → 1 → id 9 → nombre de 21 letras | [x] |
| 2 | Crear tipo de documento con id "C" | Rechazo (debe tener 2 caracteres) | 7 → 5 → `C` → descripción | [x] |
| 3 | Crear tipo de documento con id "cc" | Se guarda como "CC" | 7 → 5 → `pp` → `Prueba` → 6 (aparece `PP`) | [x] |
| 4 | Registrar cliente con nombre vacío | Rechazo | 1 → Enter en "Nombres": el lector repregunta; el constructor también lo rechaza | [x] |
| 5 | Registrar cliente con teléfono "abc123" | Rechazo | 1 → teléfono `abc123` | [x] |
| 6 | Dos clientes con el mismo documento | El segundo es rechazado | 1 → documento `1001234567` (ya existe) | [x] |
| 7 | Dos clientes con el mismo correo | El segundo es rechazado | 1 → correo `ana.perez@mail.com` | [x] |
| 8 | Cliente con país inexistente | Rechazo | 1 → id de país `77` | [x] |
| 9 | Consultar documento inexistente | Mensaje "no encontrado" | 2 → `0000` | [x] |
| 10 | Listar con 3 clientes | Ordenados por apellido | 4 → Arango, Gómez, Pérez | [x] |
| 11 | Actualizar correo a uno de otro cliente | Rechazo | 3 → `98765432` → correo `ana.perez@mail.com` | [x] |
| 12 | Actualizar correo al mismo que tenía | Se acepta | 3 → `98765432` → correo `carlos.gomez@mail.com` | [x] |

## Qué necesita el Módulo 5 de este módulo

- `Customer` (`getId()`, `getFullName()`, `getDocumentType()`, `getCountry()`).
- `CustomerService.getCustomer(documento)` para obtener el cliente que reserva.

## Pendiente para la fase de integración

- `deleteCustomer` deberá rechazarse cuando el cliente tenga reservas, preguntando al Módulo 5
  (`customerHasReservations`). El docente guiará cómo evitar la dependencia circular (sección 12
  de la guía).

## Bitácora

La bitácora es personal, como indica la guía (sección 13), y se entrega aparte al docente; no forma
parte de los archivos del proyecto.
