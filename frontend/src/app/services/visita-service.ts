import { Visita } from '../models/lecturas';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { VisitaDTO, VisitaSolicitud } from '../models/visitaDTO';

@Injectable({
  providedIn: 'root',
})
export class VisitaService {

  ruta_servidor: string = environment.apiUrl;
  recurso: string = 'visitas';

  constructor(private http: HttpClient) {}

  listByClienteId(clienteId: number) {
    return this.http.get<Visita[]>(this.ruta_servidor + '/' + this.recurso + '/cliente/' + clienteId.toString());
  }

  listByPropiedadId(propiedadId: number) {
    return this.http.get<Visita[]>(this.ruta_servidor + '/' + this.recurso + '/propiedad/' + propiedadId.toString());
  }

  add(visitaDTO: VisitaSolicitud) {
    return this.http.post<VisitaDTO>(this.ruta_servidor + '/' + this.recurso, visitaDTO);
  }

  actualizarEstado(id: number, estado: string) {
    return this.http.put<Visita>(this.ruta_servidor + '/' + this.recurso, { id, estado });
  }
}
