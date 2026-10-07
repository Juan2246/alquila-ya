import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth-guard';
import { clienteGuard } from './cliente-guard';
import { propietarioGuard } from './propietario-guard';

describe('guards', () => {
  const router = { navigate: vi.fn().mockResolvedValue(true) };
  const route = {} as ActivatedRouteSnapshot; const state = { url: '/inicio' } as RouterStateSnapshot;
  beforeEach(() => { localStorage.clear(); router.navigate.mockClear(); TestBed.configureTestingModule({ providers: [provideHttpClient(), { provide: Router, useValue: router }] }); });
  afterEach(() => localStorage.clear());
  it.each([authGuard, clienteGuard, propietarioGuard])('rechaza sesión ausente', guard => {
    expect(TestBed.runInInjectionContext(() => guard(route, state))).toBe(false); expect(router.navigate).toHaveBeenCalled();
  });
  it.each([['ROLE_CLIENTE', clienteGuard, propietarioGuard], ['ROLE_PROPIETARIO', propietarioGuard, clienteGuard]] as const)('autoriza únicamente %s', (rol, permitido, prohibido) => {
    localStorage.setItem('jwtToken', 'h.' + btoa(JSON.stringify({ exp: Date.now() / 1000 + 60 })) + '.s'); localStorage.setItem('roles', rol);
    expect(TestBed.runInInjectionContext(() => authGuard(route, state))).toBe(true);
    expect(TestBed.runInInjectionContext(() => permitido(route, state))).toBe(true);
    expect(TestBed.runInInjectionContext(() => prohibido(route, state))).toBe(false);
  });
});
