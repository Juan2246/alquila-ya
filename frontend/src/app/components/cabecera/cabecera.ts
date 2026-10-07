import { Notificacion } from '../../models/notificacion';
import { ChangeDetectorRef, Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user-service';
import { NotificacionService } from '../../services/notificacion-service';

@Component({
  selector: 'app-cabecera',
  standalone: false,
  templateUrl: './cabecera.html',
  styleUrl: './cabecera.css',
})
export class Cabecera {
  errorNotificaciones = '';
  cargandoNotificaciones = false;

  listaNotificaciones: Notificacion[] = [];
  noLeidas: number = 0;

  constructor(
    public userService: UserService,
    private notificacionService: NotificacionService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.CargarNotificaciones();
  }

  CargarNotificaciones() {
    if (!this.userService.estaLogeado()) { return; }
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { return; }

    const peticion = this.userService.esPropietario()
      ? this.notificacionService.listByPropietarioId(parseInt(perfilId))
      : this.notificacionService.listByClienteId(parseInt(perfilId));

    this.errorNotificaciones = ''; this.cargandoNotificaciones = true;
    peticion.subscribe({
      next: (data) => {
        this.cargandoNotificaciones = false;
        this.listaNotificaciones = data.sort((a, b) => b.fecha.localeCompare(a.fecha));
        this.noLeidas = data.filter(n => !n.leida).length;
        this.cdr.detectChanges();
      },
      error: () => { this.cargandoNotificaciones = false; this.errorNotificaciones = 'No se pudieron cargar las notificaciones.'; this.cdr.detectChanges(); },
    });
  }

  AbrirNotificaciones() {
    this.CargarNotificaciones();
  }

  MarcarLeida(notificacion: Notificacion) {
    if (notificacion.leida) { return; }
    this.notificacionService.marcarLeida(notificacion.id).subscribe({
      next: () => {
        notificacion.leida = true;
        this.noLeidas = this.listaNotificaciones.filter(n => !n.leida).length;
        this.cdr.detectChanges();
      },
      error: () => { this.cargandoNotificaciones = false; this.errorNotificaciones = 'No se pudieron cargar las notificaciones.'; this.cdr.detectChanges(); },
    });
  }

  Logout() {
    this.userService.logout();
    this.router.navigate(['/login']);
  }
}
