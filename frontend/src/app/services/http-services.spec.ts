import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Observable } from 'rxjs';
import { ArchivoService } from './archivo-service';
import { ComodidadService } from './comodidad-service';
import { ContratoService } from './contrato-service';
import { NotificacionService } from './notificacion-service';
import { PagoService } from './pago-service';
import { PropiedadService } from './propiedad-service';
import { ReservaService } from './reserva-service';
import { ResenaService } from './resena-service';
import { VisitaService } from './visita-service';
import { UserService } from './user-service';
import { environment } from '../../environments/environment';

describe('contratos HTTP de los diez servicios', () => {
  let http: HttpTestingController;
  beforeEach(() => { TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] }); http = TestBed.inject(HttpTestingController); });
  afterEach(() => http.verify());
  const casos: [string, string, () => Observable<unknown>, unknown?][] = [
    ['GET', '/comodidades', () => TestBed.inject(ComodidadService).listAll()],
    ['GET', '/propiedades', () => TestBed.inject(PropiedadService).listAll()],
    ['GET', '/propiedades/3', () => TestBed.inject(PropiedadService).getById(3)],
    ['GET', '/propiedades/propietario/2', () => TestBed.inject(PropiedadService).listByPropietarioId(2)],
    ['GET', '/propiedades/buscar?distrito=San%20Isidro&precioMin=0&precioMax=150&capacidad=2', () => TestBed.inject(PropiedadService).buscarConFiltros('San Isidro', 0, 150, 2)],
    ['GET', '/propiedades/3/cotizar?checkIn=2030-01-01&checkOut=2030-01-03', () => TestBed.inject(PropiedadService).cotizar(3, '2030-01-01', '2030-01-03')],
    ['DELETE', '/propiedades/3', () => TestBed.inject(PropiedadService).delete(3)],
    ['POST', '/propiedades/3/fotos', () => TestBed.inject(PropiedadService).agregarFoto(3, '/uploads/propiedades/test.png'), { url: '/uploads/propiedades/test.png' }],
    ['POST', '/propiedades/3/clausulas', () => TestBed.inject(PropiedadService).agregarClausula(3, 'Sin humo'), { texto: 'Sin humo' }],
    ['DELETE', '/propiedades/3/clausulas/5', () => TestBed.inject(PropiedadService).eliminarClausula(3, 5)],
    ['GET', '/reservas/cliente/2', () => TestBed.inject(ReservaService).listByClienteId(2)],
    ['GET', '/reservas/propiedad/3', () => TestBed.inject(ReservaService).listByPropiedadId(3)],
    ['POST', '/reservas', () => TestBed.inject(ReservaService).add({ clienteId: 2, propiedadId: 3, fechaCheckIn: '2030-01-01', fechaCheckOut: '2030-01-03' }), { clienteId: 2, propiedadId: 3, fechaCheckIn: '2030-01-01', fechaCheckOut: '2030-01-03' }],
    ['PUT', '/reservas', () => TestBed.inject(ReservaService).actualizarEstado(4, 'CANCELADA'), { id: 4, estado: 'CANCELADA' }],
    ['GET', '/contratos/cliente/2', () => TestBed.inject(ContratoService).listByClienteId(2)],
    ['GET', '/contratos/propiedad/3', () => TestBed.inject(ContratoService).listByPropiedadId(3)],
    ['GET', '/contratos/4', () => TestBed.inject(ContratoService).getById(4)],
    ['GET', '/pagos/contrato/4', () => TestBed.inject(PagoService).listByContratoId(4)],
    ['POST', '/pagos', () => TestBed.inject(PagoService).add({ contratoId: 4, monto: 200, metodo: 'Transferencia' }), { contratoId: 4, monto: 200, metodo: 'Transferencia' }],
    ['GET', '/visitas/cliente/2', () => TestBed.inject(VisitaService).listByClienteId(2)],
    ['GET', '/visitas/propiedad/3', () => TestBed.inject(VisitaService).listByPropiedadId(3)],
    ['POST', '/visitas', () => TestBed.inject(VisitaService).add({ clienteId: 2, propiedadId: 3, fecha: '2030-01-01T12:00' }), { clienteId: 2, propiedadId: 3, fecha: '2030-01-01T12:00' }],
    ['PUT', '/visitas', () => TestBed.inject(VisitaService).actualizarEstado(4, 'CANCELADA'), { id: 4, estado: 'CANCELADA' }],
    ['GET', '/resenas/propiedad/3', () => TestBed.inject(ResenaService).listByPropiedadId(3)],
    ['PUT', '/resenas/4/responder', () => TestBed.inject(ResenaService).responder(4, 'Gracias'), { respuesta: 'Gracias' }],
    ['GET', '/notificaciones/cliente/2', () => TestBed.inject(NotificacionService).listByClienteId(2)],
    ['GET', '/notificaciones/propietario/3', () => TestBed.inject(NotificacionService).listByPropietarioId(3)],
    ['PUT', '/notificaciones/4/leer', () => TestBed.inject(NotificacionService).marcarLeida(4), {}],
  ];
  it.each(casos)('%s %s y propaga la respuesta', (metodo, ruta, llamada, cuerpo) => {
    let respuesta: unknown; llamada().subscribe(dato => { respuesta = dato; });
    const req = http.expectOne(environment.apiUrl + ruta); expect(req.request.method).toBe(metodo);
    if (cuerpo) { expect(req.request.body).toEqual(cuerpo); }
    req.flush({ id: 9 }); expect(respuesta).toEqual({ id: 9 });
  });
  it.each([ArchivoService, PropiedadService])('sube multipart con campo file', servicio => {
    const archivo = new File(['png'], 'foto.png', { type: 'image/png' });
    const instancia = TestBed.inject<ArchivoService | PropiedadService>(servicio);
    (instancia instanceof ArchivoService ? instancia.subirFotoPropiedad(archivo) : instancia.subirFoto(archivo)).subscribe();
    const req = http.expectOne(environment.apiUrl + '/archivos/propiedad'); expect(req.request.body.get('file')).toBe(archivo); req.flush({ url: '/uploads/propiedades/foto.png' });
  });
  it('firma con multipart y lee el blob mediante autenticación HTTP', () => {
    TestBed.inject(ContratoService).firmar(4, new Blob(['imagen'])).subscribe();
    const req = http.expectOne(environment.apiUrl + '/contratos/4/firmar'); expect(req.request.body.get('file').name).toBe('firma.png'); req.flush({ id: 4 });
    TestBed.inject(ContratoService).leerFirma(4).subscribe(); const lectura = http.expectOne(environment.apiUrl + '/contratos/4/firma'); expect(lectura.request.responseType).toBe('blob'); lectura.flush(new Blob());
  });
  it('registra el perfil completo y no oculta fallos HTTP como listas vacías', () => {
    const entrada = { username: 'prueba', password: 'solo-entrada-de-test', rol: 'ROLE_CLIENTE', nombre: 'Ana', apellido: 'Prueba', dni: '12345678', correo: 'a@example.invalid', edad: 25, descripcion: '' };
    TestBed.inject(UserService).registro(entrada).subscribe(); const req = http.expectOne(environment.apiUrl + '/users/registro-completo'); expect(req.request.body).toEqual(entrada); req.flush({ perfilId: 2 });
    let fallo = false; TestBed.inject(ContratoService).listByPropiedadId(3).subscribe({ error: () => { fallo = true; } }); http.expectOne(environment.apiUrl + '/contratos/propiedad/3').flush({}, { status: 500, statusText: 'Error' }); expect(fallo).toBe(true);
  });
});
