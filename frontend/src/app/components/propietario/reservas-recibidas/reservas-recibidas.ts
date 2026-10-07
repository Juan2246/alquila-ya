import { archivoUrl } from '../../../shared/archivo-url';
import { Reserva } from '../../../models/reserva';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoin } from 'rxjs';
import { PropiedadService } from '../../../services/propiedad-service';
import { ReservaService } from '../../../services/reserva-service';
import { UserService } from '../../../services/user-service';
import { Propiedad } from '../../../models/propiedad';

@Component({
  selector: 'app-reservas-recibidas',
  standalone: false,
  templateUrl: './reservas-recibidas.html',
  styleUrl: './reservas-recibidas.css',
})
export class ReservasRecibidas {
  errorCarga = '';

  listaReservas: Reserva[] = [];
  readonly archivoUrl = archivoUrl;
  cargando: boolean = true;
  imagenPorDefecto: string = '/sin-foto.svg';

  constructor(
    private propiedadService: PropiedadService,
    private reservaService: ReservaService,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    this.CargarLista();
  }

  CargarLista() {
    this.errorCarga = '';
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { this.cargando = false; return; }

    this.cargando = true;
    this.propiedadService.listByPropietarioId(parseInt(perfilId)).subscribe({
      next: (propiedades: Propiedad[]) => {
        if (propiedades.length === 0) {
          this.listaReservas = [];
          this.cargando = false;
          this.cdr.detectChanges();
          return;
        }

        const llamadas = propiedades.map(p =>
          this.reservaService.listByPropiedadId(p.id)
        );

        forkJoin(llamadas).subscribe({
          next: (resultados) => {
            this.listaReservas = resultados.flat().sort((a, b) => a.fechaCheckIn.localeCompare(b.fechaCheckIn));
            this.cargando = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
            this.cargando = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  PuedeCompletar(reserva: Reserva): boolean {
    return reserva.estado === 'CONFIRMADA' && reserva.contratoEstado === 'FIRMADO'
      && new Date(reserva.fechaCheckOut + 'T00:00:00') <= new Date();
  }

  CambiarEstado(id: number, estado: 'CONFIRMADA' | 'CANCELADA') {
    this.reservaService.actualizarEstado(id, estado).subscribe({
      next: () => { this.CargarLista(); this.snackBar.open('Reserva actualizada', 'Cerrar', { duration: 2500 }); },
      error: () => { this.errorCarga = 'No se pudo actualizar la reserva.'; }
    });
  }

  MarcarCompletada(id: number) {
    this.reservaService.actualizarEstado(id, 'COMPLETADA').subscribe({
      next: () => {
        this.snackBar.open('Reserva marcada como completada', '', { duration: 1500 });
        this.CargarLista();
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open('No se pudo actualizar la reserva', '', { duration: 2500 });
      }
    });
  }
}
