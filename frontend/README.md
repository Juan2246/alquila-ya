# AlquilaYa — frontend Angular

Frontend del equipo de Arquitectura de Aplicaciones Web (UPC), integrado con el backend de [Juan2246/alquila-ya](https://github.com/Juan2246/alquila-ya). Conserva NgModules y las carpetas de componentes, servicios, modelos, guards e interceptor.

## Ejecutar

Iniciar el backend en **8080** siguiendo el [README raíz](../README.md), con PostgreSQL o el perfil efímero H2. Desde `frontend/`:

```powershell
npm.cmd ci
npm.cmd start
```

Abrir **http://localhost:4200** y registrar cuentas de propietario y cliente. Se requiere Node `^20.19.0 || ^22.12.0 || ^24.0.0`; se verificó con **24.11.1** y npm **11.6.2**. El proyecto declara npm **11.9.0** como gestor. No hace falta CLI global.

Lockfile: Angular **21.2.25**, Material/CDK **21.2.14**, CLI/build **21.2.26**, TypeScript **5.9.3**, RxJS **7.8.2**, Zone.js **0.15.1**, Vitest **4.1.11**, jsdom **27.1.0**.

## Configuración y despliegue

| Entorno | API | Imágenes |
|---|---|---|
| Desarrollo 4200 | `/alquilaya`, proxy a `http://localhost:8080` | `/uploads`, mismo proxy |
| Producción | `/alquilaya` en el mismo origen | `/uploads/propiedades` y `/uploads/perfiles` en el mismo origen |

Proxy: `src/proxy.conf.json`. Configuración pública: `src/environments/environment*.ts` (`apiUrl`, `filesBaseUrl`). No se cargan `.env`. Para una API externa, ajustar ambas rutas y `CORS_ALLOWED_ORIGINS` del servidor. Nunca incluir claves privadas en el bundle.

Publicar `dist/alquilaya-frontend/browser/` con proxy para la API/imágenes públicas y fallback a `index.html` para rutas Angular. **No exponer el directorio físico de firmas**: el frontend las descarga con JWT desde `/alquilaya/contratos/{id}/firma` y crea una URL blob temporal.

Las variables privadas (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `DDL_AUTO`) pertenecen exclusivamente al backend; configuración y puertos adicionales se describen en el README raíz.

## Flujos

- Registro con perfil, login y tratamiento de perfil fallido o sesión expirada.
- Buscar/publicar/editar propiedades, comodidades, fotos PNG/JPEG y reglas.
- Esperar fotos/reglas antes de anunciar éxito y conservar cambios pendientes para reintento.
- Cotizar fechas y reservar con contrato creado en una transacción del servidor.
- Confirmar reservas, firmar contratos, registrar pagos recibidos y completar estancias.
- Visitas, notificaciones por destinatario y reseñas con respuesta del propietario.
- Carga/error/vacío, doble envío y acciones según estado dentro de Material.

Zone.js se activa explícitamente al arrancar el NgModule porque los componentes existentes actualizan campos desde callbacks HTTP. Spring comprueba los permisos: manipular roles o IDs del navegador no concede acceso a recursos ajenos.

## Verificar

```powershell
npm.cmd run build
npm.cmd test -- --watch=false
```

59 pruebas en seis archivos cubren sesión, interceptor, guards, contratos HTTP, cotización/reserva y publicación con fallo parcial. Los presupuestos originales siguen en 500 kB/1 MB para el bundle inicial y 4/8 kB por estilo de componente. El build verificado del 7 de octubre de 2026 genera 982.17 kB iniciales: termina correctamente y conserva la advertencia de tamaño.

## Límites

El JWT sigue en `localStorage` y es accesible al JavaScript del mismo origen. La limpieza por caducidad/401 y los permisos de servidor no eliminan el riesgo de XSS; cookies HttpOnly requieren diseñar sesión/CSRF/CORS conjuntamente. Google Fonts y Material Icons requieren red; el placeholder de propiedad es local. Los pagos son registros de importes recibidos, sin pasarela; la firma es una imagen, sin certificación criptográfica.
