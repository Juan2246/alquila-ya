import { Propiedad, Propietario } from './propiedad';
import { EstadoReserva } from './lecturas';

export interface Reserva {
  id: number;
  contratoEstado: string | null;
  fechaCheckIn: string;
  fechaCheckOut: string;
  estado: EstadoReserva;
  precioTotal: number;
  fechaCreacion: string;
  cliente: Propietario;
  propiedad: Propiedad;
}
