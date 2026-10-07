import { Comodidad } from './comodidad';
import { PropiedadClausula } from './propiedadClausula';

export interface Propietario {
  id: number;
  nombre: string;
  apellido: string;
  foto: string | null;
}

export interface PropiedadFoto {
  id: number;
  url: string;
}

export interface Propiedad {
  id: number;
  titulo: string;
  descripcion: string | null;
  ubicacion: string | null;
  distrito: string | null;
  precio: number;
  habitaciones: number | null;
  capacidad: number | null;
  latitud: number | null;
  longitud: number | null;
  fechaPublicacion: string | null;
  propietario: Propietario;
  comodidades: Comodidad[];
  fotos: PropiedadFoto[];
  clausulas: PropiedadClausula[];
}
