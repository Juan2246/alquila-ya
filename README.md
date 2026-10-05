<img src="docs/brand.svg" width="76" height="76" alt="Símbolo de AlquilaYa">

# AlquilaYa

AlquilaYa es el backend de una plataforma de alquiler de propiedades que desarrollamos en el curso de Arquitectura de Aplicaciones Web de la UPC. Me encargué de la API, el modelo de datos, las reglas de negocio y la seguridad.

Quería que el recorrido de una reserva tuviera sentido de principio a fin: buscar una propiedad, revisar las fechas, calcular el precio y distinguir qué puede hacer el cliente y qué le corresponde al propietario.

**Java 25 · Spring Boot 4 · Spring Security · PostgreSQL**

[Ver el caso en mi portafolio](https://portafolio-juan-torres-puce.vercel.app/proyectos/alquila-ya)

## Funcionalidades

- Registro y autenticación de usuarios con JSON Web Tokens
- Control de acceso por roles (propietario / cliente)
- Publicación, búsqueda y filtrado de propiedades
- Reservas, visitas y cotizaciones
- Contratos con firma digital y registro de pagos
- Reseñas, favoritos y notificaciones
- Carga de archivos (fotos de propiedad, foto de perfil, firma)

## Mi rol en el proyecto

Responsable del desarrollo backend: diseño de la API REST, modelo de dominio,
lógica de negocio, persistencia y capa de seguridad.

## Puesta en marcha

Requisitos: **JDK 25** y una instancia de **PostgreSQL**. No hace falta instalar
Maven: el repositorio incluye el *Maven Wrapper*.

1. Crear la base de datos `db_alquilaya` en PostgreSQL.
2. Definir `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET` en el entorno local. La clave
   JWT debe ser aleatoria, estar codificada en Base64 y tener al menos 32 bytes.
   Se puede generar localmente con `openssl rand -base64 32`; no se debe copiar
   el resultado al repositorio.
3. Ejecutar `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).

La API queda escuchando en `http://localhost:8080/alquilaya`.

```bash
./mvnw test        # ejecutar las pruebas (usan H2 en memoria, no requieren PostgreSQL)
./mvnw package     # generar el .jar ejecutable en target/
```

`./mvnw verify` compila, ejecuta las pruebas y empaqueta. Las pruebas de seguridad
pasan por los filtros HTTP con clientes y propietarios distintos: comprueban
accesos propios y ajenos, roles de registro, respuestas sin contraseña, calendario
sin datos del huésped y rechazo de sesiones inválidas. La clave JWT y las claves
de las cuentas de prueba se generan durante la ejecución.

## Estructura del proyecto

El código sigue un **empaquetado por capas** (*package by layer*), adecuado para
el tamaño de este proyecto: una sola aplicación, un único equipo y un dominio
acotado. Cada capa depende solo de la inmediatamente inferior.

```
src/
├── main/
│   ├── java/com/arqui/alquilaya/
│   │   ├── AlquilayaApplication.java   Punto de entrada (paquete raíz: define el ámbito de escaneo)
│   │   ├── config/                     Infraestructura (recursos estáticos) y carga de datos de ejemplo (DataSeeder)
│   │   ├── security/                   Filtro JWT, UserDetails y reglas de autorización
│   │   ├── controllers/                Capa HTTP: reciben la petición y delegan
│   │   ├── dtos/                       Contratos de entrada/salida de la API
│   │   ├── services/                   Interfaces de la lógica de negocio
│   │   │   └── impl/                   Sus implementaciones
│   │   ├── repositories/               Acceso a datos (Spring Data JPA)
│   │   ├── specifications/             Consultas dinámicas para el buscador de propiedades
│   │   ├── entities/                   Modelo de dominio persistente
│   │   └── exceptions/                 Excepciones de dominio y manejador global de errores
│   └── resources/
│       └── application.properties
└── test/
    ├── java/                           Pruebas
    └── resources/
        └── application-test.properties Perfil de pruebas con H2 en memoria
```

**DTO y entidades:** las altas y algunas consultas usan DTO; otras respuestas
conservan entidades del proyecto académico. Las contraseñas se aceptan en login
y registro, pero se excluyen de las respuestas, incluso cuando un usuario aparece
anidado dentro de un propietario o cliente.

**Por qué interfaz + implementación:** los controladores dependen de la interfaz
(`services/`), no de la clase concreta (`services/impl/`). Esto permite sustituir
una implementación o simularla en pruebas sin tocar la capa HTTP.

**Controladores delgados:** los controladores solo traducen HTTP a llamadas de
servicio. Las reglas de negocio (cotizar una estadía, firmar un contrato, armar
el perfil según el rol) viven en `services/impl/`, donde se pueden probar sin
levantar la capa web.

**Inyección por constructor:** las dependencias se declaran como campos `final`
y Lombok (`@RequiredArgsConstructor`) genera el constructor. Así ninguna clase
queda a medio construir y se pueden instanciar en pruebas sin Spring.

## Endpoints principales

Todas las rutas cuelgan de `/alquilaya`.

| Recurso | Endpoints |
|---|---|
| Usuarios | `POST /users/register` · `POST /users/login` · `GET /users/perfil` |
| Propiedades | `GET /propiedades` · `GET /propiedades/buscar` · `GET /propiedades/{id}/cotizar` · `POST` · `PUT` · `DELETE` |
| Reservas | `GET /reservas/cliente/{id}` · `GET /reservas/propiedad/{id}` · `GET /reservas/propiedad/{id}/disponibilidad` · `POST` · `PUT` |
| Visitas | `GET /visitas/cliente/{id}` · `POST` · `PUT` |
| Contratos | `GET /contratos` · `POST /contratos/{id}/firmar` |
| Pagos | `GET /pagos/contrato/{id}` · `POST /pagos` |
| Reseñas | `GET /resenas/propiedad/{id}` · `POST /resenas` |
| Favoritos | `GET /favoritos/cliente/{id}` · `POST` · `DELETE` |
| Notificaciones | `GET /notificaciones/cliente/{id}` · `PUT /{id}/leer` |
| Archivos | `POST /archivos/perfil` · `POST /archivos/propiedad` · `POST /archivos/firma` |

Salvo el registro y el login, todos los endpoints exigen la cabecera
`Authorization: Bearer <token>`.

## Configuración

Las credenciales se obtienen de variables de entorno. No hay clave JWT compartida
ni cuentas creadas automáticamente en el arranque normal.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | Conexión PostgreSQL | Base local `db_alquilaya` |
| `DB_USERNAME` | Usuario de PostgreSQL | `root` |
| `DB_PASSWORD` | Clave de PostgreSQL | Vacío; definir en el entorno |
| `JWT_SECRET` | Clave Base64 de al menos 256 bits | Obligatoria |
| `JWT_EXPIRATION_MS` | Duración de la sesión | 3 horas |
| `DDL_AUTO` | Política Hibernate del esquema | `update` |

El arranque conserva los datos. Para un despliegue gestionado, usar `validate` y
migraciones del esquema. El perfil `test` usa H2 y `create-drop`, con una clave de
firma efímera generada por las pruebas.

### Datos demo opcionales

Activar `SPRING_PROFILES_ACTIVE=demo` y definir `DEMO_OWNER_PASSWORD` y
`DEMO_CLIENT_PASSWORD` localmente. Solo en una base sin cuentas se crean un
propietario, un cliente, una propiedad y una reserva ficticios. Los usuarios de
esa demostración usan el dominio reservado `example.invalid`. No se muestran ni
se registran las claves; las cuentas existentes se conservan.

### Permisos revisados

- El registro acepta exactamente `ROLE_CLIENTE` o `ROLE_PROPIETARIO`.
- Un cliente crea y consulta sus propias reservas. Puede cancelar una reserva
  propia; confirmar y completar corresponden al dueño del inmueble.
- El detalle de una reserva lo ven su cliente y el propietario de la propiedad.
  La lista de reservas de un inmueble es privada para su dueño.
- Para ver fechas ocupadas se usa `/reservas/propiedad/{id}/disponibilidad`:
  devuelve únicamente entrada, salida y estado de reservas activas, sin personas
  ni identificadores de reserva.
- Crear, modificar o eliminar una propiedad requiere ser su propietario.
- Los tokens inválidos y las cuentas deshabilitadas no permiten autenticarse.

Este mantenimiento cubre registro, autenticación, propiedades y reservas. No es
una auditoría completa de los demás recursos académicos (contratos, pagos,
visitas, reseñas, favoritos, notificaciones y archivos).

## Estado del proyecto

Backend funcional. El frontend en Angular lo desarrolla el equipo y está en
proceso de integración al repositorio.

## Autor (backend)

Juan Sebastián Torres Sánchez
