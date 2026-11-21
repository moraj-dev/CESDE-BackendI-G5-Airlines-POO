# Airlines-poo · Grupo 5

Sistema de reservas aéreas en consola, hecho en **Java 21** con **Maven**, para el
[Curso abierto de POO con Java](https://moraj-dev.github.io/poo-java-course/).
Somos cinco estudiantes y cada uno construyó un módulo sobre el proyecto base del docente.

En esta etapa los datos viven **en memoria**: al cerrar el programa se pierden y al abrirlo se
cargan otra vez los datos de ejemplo.

## Quién hizo cada módulo

| Módulo | Responsable | Paquete | Rama |
| --- | --- | --- | --- |
| 1 · Clientes y catálogos | María Alejandra Buriticá Santillana | `customers` | `feature/modulo-1-clientes` |
| 2 · Ubicaciones | Angelly Zapata | `locations` | `feature/modulo-2-ubicaciones` |
| 3 · Aerolíneas y aplicación | Sebastián Gómez | `airlines`, `app` | `develop_Sebastian_Airlines` |
| 4 · Vuelos | Juan Esteban Montoya Rua | `flights` | `develop_Juan_Montoya` |
| 5 · Reservas | Yully Milena Varela Isaza | `reservations` | `develop_Yully_Varela` |

## Estado de la integración

Rama `develop_prueba`, 21 de noviembre de 2026.

| Módulo | Pull Request | Estado |
| --- | --- | --- |
| 3 · Aerolíneas | #2 | Integrado |
| 1 · Clientes y catálogos | #3 | Integrado |
| 2 · Ubicaciones | #4 | Integrado |
| 4 · Vuelos | #6 | Integrado, pero falta corregirlo para que compile (ver Pendientes) |
| 5 · Reservas | #5 | Integrado |
| Aplicación (`Main` y menú general) | | Pendiente |

## Requisitos

- JDK 21 LTS
- IntelliJ IDEA con soporte Maven
- Git y una cuenta de GitHub

## Cómo compilar y ejecutar

En IntelliJ IDEA:

1. **File → Open** y elegir la carpeta que tiene el `pom.xml`.
2. Esperar a que Maven importe el proyecto.
3. Revisar en **File → Project Structure → Project** que el SDK sea **21**.
4. Clic derecho sobre `com.airlinespoo.app.Main` → **Run 'Main.main()'**.

Desde la terminal:

```bash
mvn clean test
mvn clean package
java -jar target/airlines-poo-0.1.0-SNAPSHOT.jar
```

**Hoy la rama no compila** por el punto 1 de Pendientes: faltan unos `import` en el paquete
`flights`. Cuando se corrija, `Main` arranca el menú de **Clientes y catálogos** con sus datos
de ejemplo. Los menús de los otros módulos ya existen, pero todavía no están conectados a un
menú general.

## Estructura del proyecto

```text
airlines-poo/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/airlinespoo/
    │   ├── core/           Kit base del docente (no se modifica)
    │   ├── customers/      Módulo 1: países, tipos de documento y clientes
    │   ├── locations/      Módulo 2: ciudades y aeropuertos
    │   ├── airlines/       Módulo 3: aerolíneas
    │   ├── flights/        Módulo 4: vuelos
    │   ├── reservations/   Módulo 5: reservas y pasajeros
    │   └── app/            Módulo 3: Main y menú general
    └── test/java/com/airlinespoo/core/   Pruebas del kit base
```

Cada módulo tiene los paquetes `model`, `repository`, `service` y `ui`.

**Regla de dependencia:** `ui` → `service` → `repository` → `model`. Nunca al revés.

- `model`: las clases con sus datos y sus validaciones.
- `repository`: guarda y busca los objetos (CRUD). No tiene reglas.
- `service`: tiene las reglas del negocio y lanza las excepciones. No imprime.
- `ui`: el menú de consola. Es lo único que habla con el usuario.

## Qué hace cada módulo

### Módulo 1 · Clientes y catálogos (`customers`)

- Clases: `Country`, `DocumentType`, `Customer`, `CountryRepository`, `DocumentTypeRepository`,
  `CustomerRepository`, `InMemoryCustomerRepository`, `CustomerService`, `CustomerMenu`,
  `CustomerSampleData` y dos comparadores para ordenar.
- Menú: registrar cliente, consultar por documento, actualizar datos de contacto, listar, buscar
  por apellido, eliminar y un submenú de catálogos (países y tipos de documento).
- Datos de ejemplo: 5 países, 4 tipos de documento y 3 clientes.
- Más detalle en [`customers/README.md`](src/main/java/com/airlinespoo/customers/README.md).

### Módulo 2 · Ubicaciones (`locations`)

- Clases: `City`, `Airport`, `CityRepository`, `AirportRepository`, `LocationService`,
  `LocationMenu`, `LocationSampleData`.
- Menú: registrar ciudad, registrar aeropuerto, listar ciudades, listar aeropuertos por ciudad,
  buscar aeropuertos por nombre, buscar ciudad por nombre, renombrar ciudad, eliminar ciudad y
  eliminar aeropuerto.
- Datos de ejemplo: 5 ciudades y 6 aeropuertos.

### Módulo 3 · Aerolíneas (`airlines`)

- Clases: `Airline`, `AirlineRepository`, `AirlineService`, `AirlineMenu`, `AirlineSeed`.
- Menú: registrar, consultar por id, renombrar, listar, buscar por nombre y eliminar.
- Datos de ejemplo: 3 aerolíneas (Avianca, LATAM y Wingo).

### Módulo 4 · Vuelos (`flights`)

- Clases: `Flight`, `FlightReportRow`, `FlightRepository`, `FlightService`, `FlightMenu`,
  `FlightSampleData`.
- Menú: programar vuelo, consultar por código, listar, reprogramar o actualizar, cancelar,
  listar por aerolínea, listar por rango de fechas y reporte mensual por aerolínea.
- Datos de ejemplo: 8 vuelos, que usan los aeropuertos del Módulo 2 y las aerolíneas del Módulo 3.

### Módulo 5 · Reservas (`reservations`)

- Clases: `Seat`, `ReservationDetail`, `Reservation`, `ReservationRepository`,
  `ReservationService`, `ReservationMenu`.
- Menú: agregar pasajero, actualizar datos del pasajero, cambiar silla, eliminar pasajero, listar
  pasajeros, listar reservaciones, cancelar reservación y consultar reservaciones por cliente.
- Sus decisiones de diseño están en [`README_reservas.md`](README_reservas.md).

## Registro de decisiones

### Identificadores

| Clase | Quién pone el id |
| --- | --- |
| `Country` | Lo escribe el usuario. Va de 1 a 195, que es el total de países del mundo. |
| `DocumentType` | Lo escribe el usuario. Son 2 letras y se guardan en mayúscula (`cc` queda `CC`). |
| `Customer` | Es el número de documento del cliente. Lo escribe el usuario. |
| `City`, `Airline`, `Flight` | Los entrega `IdGenerator`, el usuario no los escribe. |
| `Airport` | Es un `UUID` que se genera al crear el aeropuerto. |
| Pasajero de una reserva | Lo entrega `IdGenerator` y empieza en 1. |

Ningún id tiene setter: no cambia después de creado.

### Clientes y catálogos

- El correo y la dirección son opcionales. Si no se escriben quedan en `null`.
- El correo no se puede repetir, y no importa si está en mayúscula o minúscula.
- "Actualizar contacto" solo cambia teléfono, correo y dirección.
- No se puede eliminar un país o un tipo de documento que ya tenga clientes.
- Los clientes se listan ordenados por apellido y luego por nombre, con un `Comparator`.
- El servicio depende de la interfaz `CustomerRepository` y no de la clase en memoria.

### Ubicaciones

- El nombre de una ciudad no se puede repetir.
- En una misma ciudad no puede haber dos aeropuertos con el mismo nombre.
- No se puede eliminar una ciudad que tenga aeropuertos.

### Aerolíneas

- El nombre de una aerolínea no se puede repetir y tiene máximo 50 caracteres.

### Vuelos

- El aeropuerto de salida y el de llegada deben ser diferentes.
- La fecha y hora de llegada debe ser posterior a la de salida.
- El aeropuerto y la aerolínea de un vuelo deben existir: el servicio de vuelos se lo pregunta a
  los servicios de ubicaciones y de aerolíneas.

### Reservas

- **Cómo se interpreta la fila de la silla:** la fila es una letra y se guarda en mayúscula
  (`a12` y `A12` son la misma silla). El número de la silla va de 1 a 99.
- Una reserva tiene mínimo 1 pasajero y máximo 10.
- El documento del pasajero se guarda en mayúscula, sin espacios, y no se repite dentro de la
  misma reserva.

## Recorrido de punta a punta

Guion manual para probar todo el sistema cuando esté el menú general:

1. Ubicaciones → registrar ciudad.
2. Ubicaciones → registrar aeropuerto en esa ciudad (se necesitan dos aeropuertos).
3. Aerolíneas → registrar aerolínea.
4. Vuelos → programar vuelo con esos aeropuertos y esa aerolínea.
5. Clientes → registrar cliente.
6. Reservas → crear la reserva del cliente para ese vuelo (pendiente, ver Pendientes).
7. Reservas → listar pasajeros (manifiesto).
8. Vuelos → reporte mensual por aerolínea.

## Pendientes

1. **Vuelos:** agregar los `import` de `Airport`, `Airline`, `LocationService` y `AirlineService`
   en las clases del paquete `flights`, y pasar el segundo dato en las dos llamadas a
   `requireExistingAirport` de `FlightService.listFlightsByRoute`.
2. **Aplicación:** hacer el `Main` definitivo y el menú general que une los cinco módulos.
3. **Reserva y vuelo:** la reserva ya guarda su cliente; falta que guarde su vuelo y la opción de
   crear una reserva.
4. **Reglas que cruzan módulos:** no eliminar un cliente con reservas, ni una aerolínea o un
   aeropuerto con vuelos. Los métodos de consulta ya existen (`customerHasReservations`,
   `airlineHasFlights`, `airportHasFlights`).

## Kit base (paquete `core`)

Lo entrega el docente y **no se modifica**.

| Pieza | Para qué sirve |
| --- | --- |
| `AirlinesException` | Excepción base de todo el sistema |
| `ValidationException` | Un dato no cumple una regla |
| `DuplicateException` | Id o valor único repetido |
| `NotFoundException` | No existe lo que se pidió |
| `BusinessRuleException` | Una regla entre varios objetos no se cumple |
| `Identifiable<ID>` | Toda entidad ofrece su id |
| `Repository<T, ID>` | Contrato CRUD |
| `InMemoryRepository<T, ID>` | Clase abstracta con un mapa en memoria; cada módulo la extiende |
| `IdGenerator` | Ids consecutivos con arranque y paso |
| `Validator` | Validaciones con el nombre del campo en el mensaje |
| `Menu` | Contrato de los menús de consola |
| `ConsoleInput` | Lectura del teclado con reintentos |

## Flujo de trabajo con Git

- `main`: versiones publicadas. Solo el docente.
- `develop`: integración oficial.
- `develop_prueba`: rama donde el equipo está juntando los módulos para probarlos.
- Una rama por módulo o por estudiante. El trabajo llega a la rama de integración solo con
  Pull Request.
- Formato de commits: `tipo: descripción`, con `feature`, `fix`, `refactor`, `docs`, `test` o
  `merge`.
