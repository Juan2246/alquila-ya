import { PropiedadFoto } from '../models/propiedad';
import { PropiedadClausula } from '../models/propiedadClausula';
import { ArchivoService } from './archivo-service';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Propiedad } from '../models/propiedad';
import { PropiedadDTO, PropiedadSolicitud, PropiedadActualizacion } from '../models/propiedadDTO';
import { CotizacionDTO } from '../models/cotizacionDTO';

@Injectable({
  providedIn: 'root',
})
export class PropiedadService {

  ruta_servidor: string = environment.apiUrl;
  recurso: string = 'propiedades';

  constructor(private http: HttpClient, private archivos: ArchivoService) {}

  listAll() {
    return this.http.get<Propiedad[]>(this.ruta_servidor + '/' + this.recurso);
  }

  getById(id: number) {
    return this.http.get<Propiedad>(this.ruta_servidor + '/' + this.recurso + '/' + id.toString());
  }

  listByPropietarioId(propietarioId: number) {
    return this.http.get<Propiedad[]>(this.ruta_servidor + '/' + this.recurso + '/propietario/' + propietarioId.toString());
  }

  buscarConFiltros(distrito?: string, precioMin?: number, precioMax?: number, capacidad?: number) {
    let query = '';
    const params: string[] = [];
    if (distrito) { params.push('distrito=' + encodeURIComponent(distrito)); }
    if (precioMin != null) { params.push('precioMin=' + precioMin); }
    if (precioMax != null) { params.push('precioMax=' + precioMax); }
    if (capacidad != null) { params.push('capacidad=' + capacidad); }
    if (params.length > 0) { query = '?' + params.join('&'); }
    return this.http.get<Propiedad[]>(this.ruta_servidor + '/' + this.recurso + '/buscar' + query);
  }

  cotizar(propiedadId: number, checkIn: string, checkOut: string) {
    return this.http.get<CotizacionDTO>(
      this.ruta_servidor + '/' + this.recurso + '/' + propiedadId.toString() + '/cotizar?checkIn=' + checkIn + '&checkOut=' + checkOut
    );
  }

  add(propiedadDTO: PropiedadSolicitud) {
    return this.http.post<PropiedadDTO>(this.ruta_servidor + '/' + this.recurso, propiedadDTO);
  }

  update(propiedad: PropiedadActualizacion) {
    return this.http.put<Propiedad>(this.ruta_servidor + '/' + this.recurso, propiedad);
  }

  delete(id: number) {
    return this.http.delete<void>(this.ruta_servidor + '/' + this.recurso + '/' + id.toString());
  }

  subirFoto(archivo: File) {
    return this.archivos.subirFotoPropiedad(archivo);
  }

  agregarFoto(propiedadId: number, url: string) {
    return this.http.post<PropiedadFoto>(this.ruta_servidor + '/' + this.recurso + '/' + propiedadId.toString() + '/fotos', { url });
  }

  agregarClausula(propiedadId: number, texto: string) {
    return this.http.post<PropiedadClausula>(this.ruta_servidor + '/' + this.recurso + '/' + propiedadId.toString() + '/clausulas', { texto });
  }

  eliminarClausula(propiedadId: number, clausulaId: number) {
    return this.http.delete<void>(this.ruta_servidor + '/' + this.recurso + '/' + propiedadId.toString() + '/clausulas/' + clausulaId.toString());
  }
}
