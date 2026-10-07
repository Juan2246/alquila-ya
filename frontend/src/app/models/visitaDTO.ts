export interface VisitaDTO {
  id: number;
  fecha: string;
  estado: string;
  clienteId: number;
  propiedadId: number;
  clienteNombre: string;
  propiedadTitulo: string;
}

export type VisitaSolicitud = Pick<VisitaDTO, 'fecha' | 'clienteId' | 'propiedadId'>;
