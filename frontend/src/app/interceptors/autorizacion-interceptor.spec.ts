import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { autorizacionInterceptor } from './autorizacion-interceptor';
import { environment } from '../../environments/environment';

describe('autorizacionInterceptor', () => {
  let cliente: HttpClient; let http: HttpTestingController;
  const router = { url: '/inicio', navigate: vi.fn().mockResolvedValue(true) };
  beforeEach(() => {
    localStorage.clear(); router.navigate.mockClear();
    localStorage.setItem('jwtToken', 'h.' + btoa(JSON.stringify({ exp: Date.now() / 1000 + 60 })) + '.s');
    TestBed.configureTestingModule({ providers: [provideHttpClient(withInterceptors([autorizacionInterceptor])), provideHttpClientTesting(), { provide: Router, useValue: router }] });
    cliente = TestBed.inject(HttpClient); http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => { http.verify(); localStorage.clear(); });
  it('añade Bearer solo a la API', () => {
    cliente.get(environment.apiUrl + '/propiedades').subscribe();
    const req = http.expectOne(environment.apiUrl + '/propiedades'); expect(req.request.headers.get('Authorization')).toMatch(/^Bearer /); req.flush([]);
  });
  it.each(['https://tercero.invalid/alquilaya/propiedades', '/alquilaya-falso', '/uploads/imagen.png', '/alquilaya/users/login', '/alquilaya/users/registro-completo'])('no filtra JWT a %s', url => {
    cliente.get(url).subscribe(); const req = http.expectOne(url); expect(req.request.headers.has('Authorization')).toBe(false); req.flush({});
  });
  it('limpia y redirige por 401 de la API', () => {
    cliente.get(environment.apiUrl + '/users/perfil').subscribe({ error: () => {} });
    http.expectOne(environment.apiUrl + '/users/perfil').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(localStorage.getItem('jwtToken')).toBeNull(); expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
  it.each([403, 500])('conserva sesión por error %s', status => {
    cliente.get(environment.apiUrl + '/propiedades').subscribe({ error: () => {} });
    http.expectOne(environment.apiUrl + '/propiedades').flush({}, { status, statusText: 'Error' });
    expect(localStorage.getItem('jwtToken')).not.toBeNull(); expect(router.navigate).not.toHaveBeenCalled();
  });
  it('no cierra sesión por 401 externo ni envía un token vencido', () => {
    cliente.get('https://tercero.invalid').subscribe({ error: () => {} });
    http.expectOne('https://tercero.invalid').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(localStorage.getItem('jwtToken')).not.toBeNull();
    localStorage.setItem('jwtToken', 'h.' + btoa('{"exp":1}') + '.s');
    cliente.get(environment.apiUrl + '/propiedades').subscribe(); const req = http.expectOne(environment.apiUrl + '/propiedades');
    expect(req.request.headers.has('Authorization')).toBe(false); req.flush([]);
  });
});
