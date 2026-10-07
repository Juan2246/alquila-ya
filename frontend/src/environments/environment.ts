// Configuración pública de producción: servir la API y /uploads en el mismo origen.
// Estos valores se incluyen en el bundle; nunca guardar secretos aquí.
export const environment = {
  production: true,
  apiUrl: '/alquilaya',
  filesBaseUrl: '',
};
