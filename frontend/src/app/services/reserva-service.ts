import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Reserva } from '../models/reserva';
import { ReservaDTO, ReservaSolicitud } from '../models/reservaDTO';

@Injectable({
  providedIn: 'root',
})
export class ReservaService {

  ruta_servidor: string = environment.apiUrl;
  recurso: string = 'reservas';

  constructor(private http: HttpClient) {}

  listByClienteId(clienteId: number) {
    return this.http.get<Reserva[]>(this.ruta_servidor + '/' + this.recurso + '/cliente/' + clienteId.toString());
  }

  listByPropiedadId(propiedadId: number) {
    return this.http.get<Reserva[]>(this.ruta_servidor + '/' + this.recurso + '/propiedad/' + propiedadId.toString());
  }

  add(reservaDTO: ReservaSolicitud) {
    return this.http.post<ReservaDTO>(this.ruta_servidor + '/' + this.recurso, reservaDTO);
  }

  actualizarEstado(id: number, estado: string) {
    return this.http.put<Reserva>(this.ruta_servidor + '/' + this.recurso, { id, estado });
  }
}
