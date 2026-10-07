export interface ResenaDTO {
  id: number;
  puntuacion: number;
  puntuacionLimpieza: number;
  puntuacionUbicacion: number;
  puntuacionComunicacion: number;
  comentario: string;
  clienteId: number;
  propiedadId: number;
  clienteNombre: string;
  propiedadTitulo: string;
}

export type ResenaSolicitud = Pick<ResenaDTO, 'puntuacionLimpieza' | 'puntuacionUbicacion' | 'puntuacionComunicacion' | 'comentario' | 'clienteId' | 'propiedadId'>;
