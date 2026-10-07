import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Comodidad } from '../models/comodidad';

@Injectable({
  providedIn: 'root',
})
export class ComodidadService {

  ruta_servidor: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listAll() {
    return this.http.get<Comodidad[]>(this.ruta_servidor + '/comodidades');
  }
}
