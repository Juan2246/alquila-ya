import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ArchivoService {

  ruta_servidor: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  subirFotoPropiedad(archivo: File) {
    const formData = new FormData();
    formData.append('file', archivo);
    return this.http.post<{ url: string }>(this.ruta_servidor + '/archivos/propiedad', formData);
  }
}
