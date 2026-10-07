export interface ReservaDTO {
  id: number;
  contratoId: number;
  fechaCheckIn: string;
  fechaCheckOut: string;
  estado: string;
  precioTotal: number;
  clienteId: number;
  propiedadId: number;
  clienteNombre: string;
  propiedadTitulo: string;
}

export type ReservaSolicitud = Pick<ReservaDTO, 'fechaCheckIn' | 'fechaCheckOut' | 'clienteId' | 'propiedadId'>;
