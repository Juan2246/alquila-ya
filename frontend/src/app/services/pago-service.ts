import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PagoDTO, PagoSolicitud } from '../models/pagoDTO';

@Injectable({
  providedIn: 'root',
})
export class PagoService {

  ruta_servidor: string = environment.apiUrl;
  recurso: string = 'pagos';

  constructor(private http: HttpClient) {}

  listByContratoId(contratoId: number) {
    return this.http.get<PagoDTO[]>(this.ruta_servidor + '/' + this.recurso + '/contrato/' + contratoId.toString());
  }

  add(pagoDTO: PagoSolicitud) {
    return this.http.post<PagoDTO>(this.ruta_servidor + '/' + this.recurso, pagoDTO);
  }
}
