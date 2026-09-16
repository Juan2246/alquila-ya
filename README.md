# Alquila Ya

API REST de una plataforma de alquiler de propiedades tipo Airbnb, desarrollada
en equipo para el curso de Arquitectura de Aplicaciones Web (UPC).

**Stack:** Java 25 · Spring Boot 4 · Spring Security (JWT) · Spring Data JPA · PostgreSQL

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

```bash
# 1. Crear la base de datos
createdb db_alquilaya

# 2. Configurar credenciales (no se versionan)
export DB_USERNAME=tu_usuario
export DB_PASSWORD=tu_contraseña

# 3. Arrancar
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

La API queda escuchando en `http://localhost:8080/alquilaya`.

```bash
./mvnw test        # ejecutar las pruebas (usan H2 en memoria, no requieren PostgreSQL)
./mvnw package     # generar el .jar ejecutable en target/
```

## Estructura del proyecto

El código sigue un **empaquetado por capas** (*package by layer*), adecuado para
el tamaño de este proyecto: una sola aplicación, un único equipo y un dominio
acotado. Cada capa depende solo de la inmediatamente inferior.

```
src/
├── main/
│   ├── java/com/arqui/alquilaya/
│   │   ├── AlquilayaApplication.java   Punto de entrada (paquete raíz: define el ámbito de escaneo)
│   │   ├── config/                     Configuración de infraestructura (CORS, recursos estáticos)
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

**Por qué los DTO:** las entidades JPA nunca se exponen por HTTP. Los DTO
desacoplan el contrato público de la API del modelo de base de datos, de modo que
el esquema puede evolucionar sin romper a los clientes.

**Por qué interfaz + implementación:** los controladores dependen de la interfaz
(`services/`), no de la clase concreta (`services/impl/`). Esto permite sustituir
una implementación o simularla en pruebas sin tocar la capa HTTP.

## Endpoints principales

Todas las rutas cuelgan de `/alquilaya`.

| Recurso | Endpoints |
|---|---|
| Usuarios | `POST /users/register` · `POST /users/login` · `GET /users/perfil` |
| Propiedades | `GET /propiedades` · `GET /propiedades/buscar` · `GET /propiedades/{id}/cotizar` · `POST` · `PUT` · `DELETE` |
| Reservas | `GET /reservas/cliente/{id}` · `GET /reservas/propiedad/{id}` · `POST` · `PUT` |
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

Las credenciales se leen de variables de entorno y **no se versionan**:

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_USERNAME` | Usuario de PostgreSQL | `root` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | *(vacío)* |

> **Nota:** `spring.jpa.hibernate.ddl-auto=create-drop` recrea el esquema en cada
> arranque, lo que es cómodo en desarrollo pero **borra los datos**. Para un
> despliegue real debe cambiarse a `validate` y gestionar el esquema con
> migraciones (Flyway o Liquibase).

## Estado del proyecto

Backend funcional. El frontend en Angular lo desarrolla el equipo y está en
proceso de integración al repositorio.

## Autor (backend)

Juan Sebastián Torres Sánchez
