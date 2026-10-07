import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../services/user-service';

export const clienteGuard: CanActivateFn = (route, state) => {

  const userService = inject(UserService);
  const router = inject(Router);

  if (userService.estaLogeado() && userService.esCliente()) {
    return true;
  }

  router.navigate(['/inicio']);
  return false;
};
