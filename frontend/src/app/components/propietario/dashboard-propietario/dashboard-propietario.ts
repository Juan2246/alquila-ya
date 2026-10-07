import { ChangeDetectorRef, Component } from '@angular/core';
import { forkJoin } from 'rxjs';
import { PropiedadService } from '../../../services/propiedad-service';
import { ReservaService } from '../../../services/reserva-service';
import { ResenaService } from '../../../services/resena-service';
import { UserService } from '../../../services/user-service';
import { Propiedad } from '../../../models/propiedad';

@Component({
  selector: 'app-dashboard-propietario',
  standalone: false,
  templateUrl: './dashboard-propietario.html',
  styleUrl: './dashboard-propietario.css',
})
export class DashboardPropietario {
  errorCarga = '';

  cargando: boolean = true;
  totalPropiedades: number = 0;
  totalReservas: number = 0;
  ingresosTotales: number = 0;
  calificacionPromedio: number | null = null;
  totalResenas: number = 0;
  listaPropiedades: Propiedad[] = [];

  constructor(
    private propiedadService: PropiedadService,
    private reservaService: ReservaService,
    private resenaService: ResenaService,
    private userService: UserService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { this.cargando = false; return; }

    this.propiedadService.listByPropietarioId(parseInt(perfilId)).subscribe({
      next: (propiedades: Propiedad[]) => {
        this.listaPropiedades = propiedades;
        this.totalPropiedades = propiedades.length;

        if (propiedades.length === 0) {
          this.cargando = false;
          this.cdr.detectChanges();
          return;
        }

        const reservasPorPropiedad = propiedades.map(p =>
          this.reservaService.listByPropiedadId(p.id)
        );
        const resenasPorPropiedad = propiedades.map(p =>
          this.resenaService.listByPropiedadId(p.id)
        );

        forkJoin({
          reservas: forkJoin(reservasPorPropiedad),
          resenas: forkJoin(resenasPorPropiedad),
        }).subscribe({
          next: (resultado) => {
            const todasReservas = resultado.reservas.flat();
            const todasResenas = resultado.resenas.flat();

            const reservasValidas = todasReservas.filter(r => r.estado !== 'CANCELADA');
            this.totalReservas = reservasValidas.length;
            this.ingresosTotales = reservasValidas.reduce((acc, r) => acc + (r.precioTotal || 0), 0);

            this.totalResenas = todasResenas.length;
            if (todasResenas.length > 0) {
              const suma = todasResenas.reduce((acc, r) => acc + (r.puntuacion || 0), 0);
              this.calificacionPromedio = Math.round((suma / todasResenas.length) * 10) / 10;
            }

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
}
