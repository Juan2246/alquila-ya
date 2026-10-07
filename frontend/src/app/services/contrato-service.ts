import { Contrato } from '../models/lecturas';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ContratoService {

  ruta_servidor: string = environment.apiUrl;
  recurso: string = 'contratos';

  constructor(private http: HttpClient) {}

  listByClienteId(clienteId: number) {
    return this.http.get<Contrato[]>(this.ruta_servidor + '/' + this.recurso + '/cliente/' + clienteId.toString());
  }

  listByPropiedadId(propiedadId: number) {
    return this.http.get<Contrato[]>(this.ruta_servidor + '/' + this.recurso + '/propiedad/' + propiedadId.toString());
  }

  getById(id: number) {
    return this.http.get<Contrato>(this.ruta_servidor + '/' + this.recurso + '/' + id.toString());
  }

  leerFirma(id: number) {
    return this.http.get(this.ruta_servidor + '/' + this.recurso + '/' + id + '/firma', { responseType: 'blob' });
  }

  firmar(id: number, imagenBlob: Blob) {
    const formData = new FormData();
    formData.append('file', imagenBlob, 'firma.png');
    return this.http.post<Contrato>(this.ruta_servidor + '/' + this.recurso + '/' + id.toString() + '/firmar', formData);
  }
}
