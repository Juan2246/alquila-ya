import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PublicarPropiedad } from './publicar-propiedad';
import { environment } from '../../../../environments/environment';

describe('PublicarPropiedad / persistencia completa', () => {
  let vista: PublicarPropiedad; let http: HttpTestingController;
  const router = { navigate: vi.fn().mockResolvedValue(true) };
  beforeEach(() => {
    router.navigate.mockClear(); localStorage.setItem('perfilId', '2');
    TestBed.configureTestingModule({ declarations: [PublicarPropiedad], providers: [provideHttpClient(), provideHttpClientTesting(),
      { provide: ActivatedRoute, useValue: { snapshot: { params: {} } } }, { provide: Router, useValue: router },
      { provide: MatSnackBar, useValue: { open: vi.fn() } }] }).overrideComponent(PublicarPropiedad, { set: { template: '' } });
    const fixture = TestBed.createComponent(PublicarPropiedad); vista = fixture.componentInstance; http = TestBed.inject(HttpTestingController); fixture.detectChanges();
    http.expectOne(environment.apiUrl + '/comodidades').flush([]);
    vista.publicarForm.setValue({ titulo: 'Casa', descripcion: 'Prueba', ubicacion: 'Dirección', distrito: 'Lima', precio: 100, habitaciones: 2, capacidad: 3 });
  });
  afterEach(() => { http.verify(); localStorage.clear(); });
  it('espera foto y cláusulas antes de navegar o anunciar éxito', () => {
    vista.archivoSeleccionado = new File(['png'], 'foto.png', { type: 'image/png' }); vista.clausulasNuevas = ['Sin humo']; vista.Guardar();
    http.expectOne(environment.apiUrl + '/propiedades').flush({ id: 3 }); expect(router.navigate).not.toHaveBeenCalled();
    http.expectOne(environment.apiUrl + '/archivos/propiedad').flush({ url: '/uploads/propiedades/test.png' }); expect(router.navigate).not.toHaveBeenCalled();
    http.expectOne(environment.apiUrl + '/propiedades/3/fotos').flush({ id: 4, url: '/uploads/propiedades/test.png' }); expect(router.navigate).not.toHaveBeenCalled();
    http.expectOne(environment.apiUrl + '/propiedades/3/clausulas').flush({ id: 5, texto: 'Sin humo' });
    expect(router.navigate).toHaveBeenCalledWith(['/mis-propiedades']); expect(vista.guardando).toBe(false);
  });
  it('conserva ID y cláusulas pendientes al fallar; reintenta con PUT', () => {
    vista.clausulasNuevas = ['Sin humo']; vista.Guardar(); http.expectOne(environment.apiUrl + '/propiedades').flush({ id: 3 });
    http.expectOne(environment.apiUrl + '/propiedades/3/clausulas').flush({}, { status: 500, statusText: 'Error' });
    expect(vista.id).toBe(3); expect(vista.clausulasNuevas).toEqual(['Sin humo']); expect(vista.errorCarga).toContain('pendientes'); expect(router.navigate).not.toHaveBeenCalled();
    vista.Guardar(); const req = http.expectOne(environment.apiUrl + '/propiedades'); expect(req.request.method).toBe('PUT'); expect(req.request.body.comodidadIds).toEqual([]); req.flush({ id: 3 });
    http.expectOne(environment.apiUrl + '/propiedades/3/clausulas').flush({ id: 5, texto: 'Sin humo' }); expect(vista.clausulasNuevas).toEqual([]);
  });
});
