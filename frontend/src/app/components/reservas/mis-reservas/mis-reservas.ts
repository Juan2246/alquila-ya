import { archivoUrl } from '../../../shared/archivo-url';
import { Visita } from '../../../models/lecturas';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Reserva } from '../../../models/reserva';
import { ReservaService } from '../../../services/reserva-service';
import { VisitaService } from '../../../services/visita-service';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-mis-reservas',
  standalone: false,
  templateUrl: './mis-reservas.html',
  styleUrl: './mis-reservas.css',
})
export class MisReservas {
  errorCarga = '';

  listaReservas: Reserva[] = [];
  listaVisitas: Visita[] = [];
  readonly archivoUrl = archivoUrl;
  cargando: boolean = true;
  imagenPorDefecto: string = '/sin-foto.svg';

  constructor(
    private reservaService: ReservaService,
    private visitaService: VisitaService,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    this.CargarLista();
    this.CargarVisitas();
  }

  CargarVisitas() {
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { return; }

    this.visitaService.listByClienteId(parseInt(perfilId)).subscribe({
      next: (data) => {
        this.listaVisitas = data;
        this.cdr.detectChanges();
      },
      error: (err) => { this.errorCarga = 'No se pudo cargar esta información. Reintenta la consulta.'; },
    });
  }

  CargarLista() {
    this.errorCarga = '';
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { this.cargando = false; return; }

    this.cargando = true;
    this.reservaService.listByClienteId(parseInt(perfilId)).subscribe({
      next: (data: Reserva[]) => {
        this.listaReservas = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  Cancelar(id: number) {
    this.reservaService.actualizarEstado(id, 'CANCELADA').subscribe({
      next: () => {
        this.snackBar.open('Reserva cancelada', '', { duration: 1500 });
        this.CargarLista();
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open('No se pudo cancelar la reserva', '', { duration: 2500 });
      }
    });
  }
}
