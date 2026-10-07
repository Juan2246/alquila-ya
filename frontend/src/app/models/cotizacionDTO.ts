export interface CotizacionDTO {
  propiedadId: number;
  propiedadTitulo: string;
  fechaCheckIn: string;
  fechaCheckOut: string;
  noches: number;
  precioPorNoche: number;
  precioTotal: number;
}
