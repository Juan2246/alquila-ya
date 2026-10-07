import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user-service';
import { environment } from '../../environments/environment';

export function token(exp: number): string { return 'header.' + btoa(JSON.stringify({ exp })) + '.signature'; }

describe('UserService / sesión', () => {
  let servicio: UserService;
  let http: HttpTestingController;
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servicio = TestBed.inject(UserService); http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => { http.verify(); localStorage.clear(); });
  it('guarda el login y reemplaza la sesión anterior', () => {
    localStorage.setItem('perfilId', '99');
    servicio.login({ username: 'prueba', password: 'entrada-de-prueba' }).subscribe();
    expect(localStorage.getItem('perfilId')).toBeNull();
    const req = http.expectOne(environment.apiUrl + '/users/login');
    expect(req.request.method).toBe('POST');
    req.flush({ jwtToken: token(Date.now() / 1000 + 60), id: 4, roles: 'ROLE_CLIENTE' });
    expect(servicio.estaLogeado()).toBe(true); expect(servicio.esCliente()).toBe(true);
  });
  it('obtiene el perfil y distingue cuenta de perfil', () => {
    servicio.getPerfil().subscribe();
    http.expectOne(environment.apiUrl + '/users/perfil').flush({ userId: 4, perfilId: 7, nombre: 'Ana', apellido: 'Prueba' });
    expect(servicio.getPerfilIdLogeado()).toBe('7'); expect(servicio.getNombreLogeado()).toBe('Ana Prueba');
  });
  it('rechaza un perfil incompleto', () => {
    let fallo = false;
    servicio.getPerfil().subscribe({ error: () => { fallo = true; } });
    http.expectOne(environment.apiUrl + '/users/perfil').flush({ perfilId: null });
    expect(fallo).toBe(true); expect(servicio.getPerfilIdLogeado()).toBeNull();
  });
  it.each(['roto', 'a.e30=.c', token(1)])('limpia una sesión inválida %s', jwt => {
    localStorage.setItem('jwtToken', jwt); localStorage.setItem('nombre', 'Anterior');
    expect(servicio.estaLogeado()).toBe(false); expect(localStorage.getItem('nombre')).toBeNull();
  });
  it('logout conserva datos ajenos y los roles se comparan completos', () => {
    localStorage.setItem('tema', 'oscuro'); localStorage.setItem('roles', 'ROLE_CLIENTE_FALSO');
    expect(servicio.esCliente()).toBe(false); servicio.logout(); expect(localStorage.getItem('tema')).toBe('oscuro');
  });
});
