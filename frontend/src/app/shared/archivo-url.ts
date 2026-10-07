import { environment } from '../../environments/environment';
export function archivoUrl(ruta: string | null | undefined): string {
  if (!ruta) { return '/sin-foto.svg'; }
  if (/^https?:\/\//i.test(ruta)) { return ruta; }
  if (!ruta.startsWith('/uploads/')) { return '/sin-foto.svg'; }
  return environment.filesBaseUrl.replace(/\/$/, '') + ruta;
}
