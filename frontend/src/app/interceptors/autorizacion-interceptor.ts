import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { UserService } from '../services/user-service';
import { environment } from '../../environments/environment';

export const autorizacionInterceptor: HttpInterceptorFn = (req, next) => {

  // No enviar el JWT a terceros ni a rutas que solo comparten un prefijo.
  const apiUrl = new URL(environment.apiUrl, window.location.origin);
  const requestUrl = new URL(req.url, window.location.origin);
  const apiPath = apiUrl.pathname.replace(/\/$/, '');
  const esApi = requestUrl.origin === apiUrl.origin &&
    (requestUrl.pathname === apiPath || requestUrl.pathname.startsWith(apiPath + '/'));
  const esPublica = [apiPath + '/users/login', apiPath + '/users/register',
    apiPath + '/users/registro-completo'].includes(requestUrl.pathname.replace(/\/$/, ''));

  if (!esApi || esPublica) {
    return next(req);
  }

  const userService = inject(UserService);
  const router = inject(Router);
  const token = userService.estaLogeado() ? userService.getJwtTokenLogeado() : null;
  const solicitud = token ? req.clone({ setHeaders: { Authorization: 'Bearer ' + token } }) : req;
  return next(solicitud).pipe(catchError((error: unknown) => {
    if (error instanceof HttpErrorResponse && error.status === 401) {
      userService.logout();
      if (!router.url.startsWith('/login')) { void router.navigate(['/login']); }
    }
    return throwError(() => error);
  }));
};
