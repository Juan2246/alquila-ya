import { Propiedad, Propietario } from './propiedad';
export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA';
export type EstadoContrato = 'PENDIENTE' | 'ACTIVO' | 'FIRMADO' | 'FINALIZADO' | 'CANCELADO';
export type EstadoVisita = 'PENDIENTE' | 'COMPLETADA' | 'CANCELADA';
export interface Contrato {
  id: number; fechaInicio: string; fechaFin: string; estado: EstadoContrato;
  firmaImagenUrl: string | null; cliente: Propietario; propiedad: Propiedad; reservaId: number | null;
}
export interface Visita { id: number; fecha: string; estado: EstadoVisita; cliente: Propietario; propiedad: Propiedad; }
export interface Resena {
  id: number; puntuacion: number; puntuacionLimpieza: number; puntuacionUbicacion: number;
  puntuacionComunicacion: number; comentario: string; fecha: string; cliente: Propietario;
  propiedad: Pick<Propiedad, 'id' | 'titulo'>;
  respuestaPropietario: string | null;
}
