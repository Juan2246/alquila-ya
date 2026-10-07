import { Notificacion } from '../models/notificacion';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class NotificacionService {

  ruta_servidor: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listByClienteId(clienteId: number) {
    return this.http.get<Notificacion[]>(this.ruta_servidor + '/notificaciones/cliente/' + clienteId.toString());
  }

  listByPropietarioId(propietarioId: number) {
    return this.http.get<Notificacion[]>(this.ruta_servidor + '/notificaciones/propietario/' + propietarioId.toString());
  }

  marcarLeida(id: number) {
    return this.http.put<Notificacion>(this.ruta_servidor + '/notificaciones/' + id.toString() + '/leer', {});
  }
}
