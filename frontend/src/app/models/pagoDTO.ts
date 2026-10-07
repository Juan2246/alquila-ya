export interface PagoDTO {
  id: number;
  monto: number;
  fecha: string;
  estado: string;
  metodo: string;
  contratoId: number;
}

export type PagoSolicitud = Pick<PagoDTO, 'monto' | 'metodo' | 'contratoId'>;
