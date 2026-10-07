export interface RegistroDTO {
  username: string;
  password: string;
  rol: string; // "ROLE_CLIENTE" o "ROLE_PROPIETARIO"
  nombre: string;
  apellido: string;
  dni: string;
  correo: string;
  edad: number;
  descripcion: string;
}
