import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DetallePropiedad } from './detalle-propiedad';
import { environment } from '../../../../environments/environment';

describe('DetallePropiedad / reserva', () => {
  let vista: DetallePropiedad; let http: HttpTestingController;
  const router = { navigate: vi.fn().mockResolvedValue(true) };
  beforeEach(() => {
    router.navigate.mockClear(); localStorage.setItem('perfilId', '2');
    TestBed.configureTestingModule({ declarations: [DetallePropiedad], providers: [provideHttpClient(), provideHttpClientTesting(),
      { provide: ActivatedRoute, useValue: { snapshot: { params: { id: '3' } } } }, { provide: Router, useValue: router },
      { provide: MatSnackBar, useValue: { open: vi.fn() } }] }).overrideComponent(DetallePropiedad, { set: { template: '' } });
    const fixture = TestBed.createComponent(DetallePropiedad); vista = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController); fixture.detectChanges();
    http.expectOne(environment.apiUrl + '/propiedades/3').flush({ id: 3 });
    http.expectOne(environment.apiUrl + '/resenas/propiedad/3').flush([]);
  });
  afterEach(() => { http.verify(); localStorage.clear(); });
  it('no reserva con el formulario vacío o fechas invertidas', () => {
    vista.Reservar(); vista.reservaForm.setValue({ checkIn: '2099-01-04', checkOut: '2099-01-01' }); vista.Reservar();
    http.expectNone(environment.apiUrl + '/reservas'); expect(router.navigate).not.toHaveBeenCalled();
  });
  it('invalida cotización y descarta respuestas de fechas anteriores', () => {
    vista.reservaForm.setValue({ checkIn: '2099-01-01', checkOut: '2099-01-03' }); vista.Cotizar();
    const req = http.expectOne(r => r.url.includes('/cotizar'));
    vista.reservaForm.patchValue({ checkOut: '2099-01-05' });
    req.flush({ precioTotal: 200 }); expect(vista.cotizacion).toBeNull();
    vista.Cotizar(); http.expectOne(r => r.url.includes('/cotizar')).flush({ precioTotal: 400 });
    expect(vista.cotizacion?.precioTotal).toBe(400);
    vista.reservaForm.patchValue({ checkIn: '2099-01-02' }); expect(vista.cotizacion).toBeNull();
  });
  it('reserva y contrato usan una sola petición y evitan doble clic', () => {
    vista.reservaForm.setValue({ checkIn: '2099-01-01', checkOut: '2099-01-03' }); vista.Reservar(); vista.Reservar();
    const req = http.expectOne(environment.apiUrl + '/reservas'); expect(req.request.body).toEqual({ clienteId: 2, propiedadId: 3, fechaCheckIn: '2099-01-01', fechaCheckOut: '2099-01-03' });
    req.flush({ id: 7, contratoId: 9, precioTotal: 200 });
    http.expectNone(environment.apiUrl + '/contratos'); expect(router.navigate).toHaveBeenCalledWith(['/mis-contratos']); expect(vista.reservando).toBe(false);
  });
});
