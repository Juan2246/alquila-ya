export interface PropiedadDTO {
  id: number;
  titulo: string;
  descripcion: string;
  ubicacion: string;
  distrito: string;
  precio: number;
  habitaciones: number;
  capacidad: number;
  latitud: number;
  longitud: number;
  propietarioId: number;
  propietarioNombre: string;
  comodidadIds: number[];
}

export type PropiedadSolicitud = Omit<PropiedadDTO, 'id' | 'propietarioNombre'>;
export type PropiedadActualizacion = PropiedadSolicitud & { id: number };
