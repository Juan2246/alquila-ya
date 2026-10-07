import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs';
import { LoginDTO } from '../models/userDTO';
import { TokenDTO } from '../models/tokenDTO';
import { RegistroDTO } from '../models/registroDTO';
import { PerfilDTO } from '../models/perfilDTO';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  ruta_servidor: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  login(userDTO: LoginDTO) {
    this.logout();
    return this.http.post<TokenDTO>(this.ruta_servidor + '/users/login', userDTO).pipe(
      tap((data: TokenDTO) => {
        localStorage.setItem('jwtToken', data.jwtToken);
        localStorage.setItem('id', data.id.toString());
        localStorage.setItem('roles', data.roles);
      })
    );
  }

  registro(registroDTO: RegistroDTO) {
    return this.http.post<PerfilDTO>(this.ruta_servidor + '/users/registro-completo', registroDTO);
  }

  getPerfil() {
    return this.http.get<PerfilDTO>(this.ruta_servidor + '/users/perfil').pipe(
      tap((perfil: PerfilDTO) => {
        if (!perfil.perfilId) { throw new Error('La cuenta no tiene un perfil de cliente o propietario'); }
        localStorage.setItem('perfilId', perfil.perfilId ? perfil.perfilId.toString() : '');
        localStorage.setItem('nombre', perfil.nombre || '');
        localStorage.setItem('apellido', perfil.apellido || '');
      })
    );
  }

  logout() {
    ['jwtToken', 'id', 'roles', 'perfilId', 'nombre', 'apellido']
      .forEach(key => localStorage.removeItem(key));
  }

  getRolesLogeado() {
    return localStorage.getItem('roles');
  }

  getJwtTokenLogeado() {
    return localStorage.getItem('jwtToken');
  }

  getPerfilIdLogeado() {
    return localStorage.getItem('perfilId');
  }

  getNombreLogeado() {
    const nombre = localStorage.getItem('nombre') || '';
    const apellido = localStorage.getItem('apellido') || '';
    return (nombre + ' ' + apellido).trim();
  }

  estaLogeado(): boolean {
    const token = this.getJwtTokenLogeado();
    if (!token) { return false; }
    try {
      const partes = token.split('.');
      if (partes.length !== 3) { throw new Error('JWT malformado'); }
      const base64 = partes[1].replace(/-/g, '+').replace(/_/g, '/');
      const payload = JSON.parse(atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, '=')));
      // Solo controla la caducidad en la interfaz; el servidor verifica la firma.
      if (typeof payload.exp === 'number' && Number.isFinite(payload.exp) &&
          payload.exp * 1000 > Date.now()) {
        return true;
      }
    } catch {
      // Una sesión dañada se trata como una sesión vencida.
    }
    this.logout();
    return false;
  }

  esCliente(): boolean {
    const roles = this.getRolesLogeado();
    return roles !== null && roles.split(';').includes('ROLE_CLIENTE');
  }

  esPropietario(): boolean {
    const roles = this.getRolesLogeado();
    return roles !== null && roles.split(';').includes('ROLE_PROPIETARIO');
  }
}
