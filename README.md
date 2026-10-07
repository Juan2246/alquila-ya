<img src="docs/brand.svg" width="76" height="76" alt="Símbolo de AlquilaYa">

# AlquilaYa

Plataforma de alquiler de propiedades del equipo del curso de **Arquitectura de Aplicaciones Web de la UPC**. Reúne el backend Spring Boot y el frontend Angular en `frontend/`.

**Juan Sebastián Torres Sánchez** es el responsable del backend documentado en el proyecto original: API REST, modelo de dominio, reglas de negocio, persistencia y seguridad. El frontend es trabajo del equipo; esta integración conserva su estructura de NgModules, componentes y servicios. El historial identifica los cambios de mantenimiento e integración asistidos.

[Caso en el portafolio de Juan](https://portafolio-juan-torres-puce.vercel.app/proyectos/alquila-ya) · [Guía del frontend](frontend/README.md)

## Funcionalidades

- Registro transaccional de cliente/propietario con perfil y autenticación JWT.
- Publicación y búsqueda de propiedades, comodidades, fotos y reglas.
- Cotización en servidor; reserva y contrato creados en una transacción.
- Confirmación de reservas, firma como imagen privada y registro de pagos recibidos.
- Visitas, reseñas de estancias completadas y respuestas del propietario.
- Notificaciones de reservas para ambos perfiles y favoritos en la API.
- Permisos por pertenencia, DTO de lectura mínimos y validación real de imágenes.

Los pagos registran importes recibidos, sin pasarela bancaria. La firma dibujada es una imagen asociada al contrato; no es una firma digital certificada.

## Stack

Java **25** (Temurin **25.0.4**), Spring Boot **4.0.5**, Spring Security, Spring Data JPA/Hibernate, JJWT **0.13.0** y Maven **3.9.11**. PostgreSQL es la base configurada para persistencia; H2 se usa en pruebas y en el perfil local opcional.

Angular **21.2.25**, Material/CDK **21.2.14**, CLI/build **21.2.26**, TypeScript **5.9.3**, RxJS **7.8.2** y Zone.js **0.15.1**. Entorno verificado: Node **24.11.1**, npm **11.6.2**. El frontend declara npm 11.9.0 como gestor; el lockfile fija sus dependencias.

## Levantar backend y frontend

Requisitos: JDK 25, Node compatible (`^20.19.0 || ^22.12.0 || ^24.0.0`) y PostgreSQL, salvo que se use H2. El Maven Wrapper está incluido; también puede usarse Maven 3.9.11 instalado.

1. Crear la base PostgreSQL `db_alquilaya`.
2. Definir `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET` en el entorno privado. La clave JWT debe ser aleatoria, Base64 y de al menos 32 bytes; puede generarse localmente con `openssl rand -base64 32`. No copiar el resultado al repositorio.
3. En la raíz, ejecutar:

   ```powershell
   $env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot'
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   .\mvnw.cmd spring-boot:run
   ```

4. En otra terminal:

   ```powershell
   cd frontend
   npm.cmd ci
   npm.cmd start
   ```

5. Abrir **http://localhost:4200** y registrar las cuentas desde la interfaz. La API escucha en **http://localhost:8080/alquilaya**; el proxy Angular reenvía `/alquilaya/**` y `/uploads/**` al puerto 8080.

En Linux/macOS usar `./mvnw` y `npm`; ajustar `JAVA_HOME` a la instalación local.

### Desarrollo sin PostgreSQL

Con `JWT_SECRET` definido, ejecutar:

```powershell
.\mvnw.cmd -Plocal-h2 spring-boot:run "-Dspring-boot.run.profiles=local-h2"
```

H2 pierde los datos al apagar el servidor; sus imágenes se guardan en `target/local-uploads`. Se necesitan tanto el perfil Maven (driver H2) como el perfil Spring (configuración). No usarlo en producción.

### Variables del servidor

| Variable | Uso |
|---|---|
| `JAVA_HOME` | Instalación local de JDK 25 |
| `DB_URL` | URL JDBC PostgreSQL; base local `db_alquilaya` en 5432 por defecto |
| `DB_USERNAME`, `DB_PASSWORD` | Credenciales privadas de PostgreSQL |
| `JWT_SECRET` | Clave privada Base64 obligatoria, mínimo 256 bits |
| `JWT_EXPIRATION_MS` | Duración del token; tres horas por defecto |
| `DDL_AUTO` | Política Hibernate; `update` por defecto, `validate` con migraciones gestionadas |
| `CORS_ALLOWED_ORIGINS` | Orígenes separados por comas; `http://localhost:4200` por defecto |
| `SERVER_PORT` | Puerto del backend; 8080 por defecto |
| `FILE_UPLOAD_DIR` | Almacenamiento; `uploads` por defecto |
| `SPRING_PROFILES_ACTIVE` | Perfiles opcionales de Spring |
| `DEMO_OWNER_PASSWORD`, `DEMO_CLIENT_PASSWORD` | Claves requeridas solo por el perfil `demo` |

No versionar valores reales ni `.env`; Spring y Angular no cargan esos archivos automáticamente. `environment*.ts` contiene configuración pública compilada, nunca claves del servidor.

El arranque normal no crea cuentas. El perfil `demo`, con sus dos claves, crea datos ficticios solo si no existen usuarios: `propietario.demo@example.invalid`, `cliente.demo@example.invalid`, una propiedad y una reserva con contrato pendiente. Conserva las cuentas existentes. Roles y comodidades se inicializan sin credenciales demo.

## Reglas de negocio y seguridad

Reserva y contrato inician **PENDIENTES**. El dueño confirma la reserva y activa el contrato; el cliente puede firmarlo una sola vez. El dueño registra pagos recibidos sobre contratos **FIRMADOS**, sin superar el saldo. Completar una reserva requiere llegar al check-out con contrato firmado y lo deja **FINALIZADO**. Cancelar una reserva pendiente o confirmada cancela también su contrato; una estancia completada no se puede cancelar. El bloqueo de la propiedad impide reservas concurrentes solapadas.

Contratos, pagos, visitas y reservas requieren pertenencia; las notificaciones, ser su destinatario. Confirmar/completar, responder reseñas y registrar pagos corresponden al dueño. Crear reseñas exige una estancia completada del cliente autenticado. Los guards orientan la interfaz; Spring impone los permisos.

Los DTO de lectura excluyen cuentas, DNI y correos de terceros; la disponibilidad devuelve solo fechas y estado. PNG/JPEG se decodifican y recodifican en servidor, con límites de 10 MB y 20 megapíxeles. Las firmas se descargan con JWT desde `/alquilaya/contratos/{id}/firma`; `/uploads/firmas/**` está bloqueado y no tiene manejador estático. No servir directamente el directorio físico de firmas desde un proxy.

El JWT permanece en `localStorage`: la caducidad/401 limpia la sesión y el interceptor restringe su envío a la API, pero sigue expuesto ante un XSS. Migrar a cookies HttpOnly exige coordinar sesión, CSRF y CORS.

## API y estructura

Las rutas cuelgan de `/alquilaya`; exigen Bearer JWT salvo login y registro.

| Recurso | Operaciones principales |
|---|---|
| Usuarios | `/users/registro-completo`, `/users/login`, `/users/perfil` |
| Comodidades | `GET /comodidades` |
| Propiedades | CRUD, `/buscar`, `/{id}/cotizar`, `/{id}/fotos`, `/{id}/clausulas` |
| Reservas | Alta/estados, listas por cliente/propiedad y disponibilidad |
| Contratos | Listas propias/por cliente/propiedad, `/{id}/firmar`, `/{id}/firma` |
| Pagos | Lista por contrato y registro de importe recibido |
| Visitas | Alta, listas por cliente/propiedad y cambio de estado |
| Reseñas | Lista por propiedad, alta y `/{id}/responder` |
| Notificaciones | Listas por cliente/propietario y `/{id}/leer` |
| Archivos / favoritos | Subidas multipart / lista por cliente, alta y baja |

`/users/register` se conserva como alta de cuenta sin perfil; la SPA usa el registro completo. Crear o actualizar contratos directamente devuelve conflicto: su ciclo depende de reserva y firma.

Backend por capas en `src/main/java/com/arqui/alquilaya`: `controllers`, `dtos`, `services/impl`, `repositories`, `entities`, `security`, `config`, `exceptions`. Frontend en `frontend/src/app`: `components`, `services`, `models`, `guards`, `interceptors`, `shared`, `modules`.

## Verificación y límites

```powershell
.\mvnw.cmd verify
cd frontend
npm.cmd run build
npm.cmd test -- --watch=false
```

`verify` compila, ejecuta todas las pruebas y empaqueta. La integración inicia Tomcat en puerto aleatorio con HTTP, JWT y H2 reales: registro, propiedad/fotos/reglas, reserva/contrato, firma privada, pago, notificación, visita, reseña, concurrencia y rechazo de accesos ajenos. Las claves de prueba se generan en ejecución.

Angular tiene 59 pruebas con Vitest/jsdom. El bundle inicial verificado de 982.17 kB pasa el límite original de error de 1 MB y avisa al superar 500 kB. Google Fonts/Material Icons requieren red. H2 no sustituye una prueba de migración con PostgreSQL y datos preexistentes. Los contratos antiguos sin reserva necesitan conciliación para el nuevo ciclo.
