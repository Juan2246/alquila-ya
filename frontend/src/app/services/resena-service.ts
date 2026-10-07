import { Resena } from '../models/lecturas';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ResenaDTO, ResenaSolicitud } from '../models/resenaDTO';

@Injectable({
  providedIn: 'root',
})
export class ResenaService {

  ruta_servidor: string = environment.apiUrl;
  recurso: string = 'resenas';

  constructor(private http: HttpClient) {}

  listByPropiedadId(propiedadId: number) {
    return this.http.get<Resena[]>(this.ruta_servidor + '/' + this.recurso + '/propiedad/' + propiedadId.toString());
  }

  add(resenaDTO: ResenaSolicitud) {
    return this.http.post<Resena>(this.ruta_servidor + '/' + this.recurso, resenaDTO);
  }

  responder(id: number, respuesta: string) {
    return this.http.put<Resena>(this.ruta_servidor + '/' + this.recurso + '/' + id.toString() + '/responder', { respuesta });
  }
}
