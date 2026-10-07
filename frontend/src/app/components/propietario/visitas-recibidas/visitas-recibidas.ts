import { Visita } from '../../../models/lecturas';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoin } from 'rxjs';
import { PropiedadService } from '../../../services/propiedad-service';
import { VisitaService } from '../../../services/visita-service';
import { UserService } from '../../../services/user-service';
import { Propiedad } from '../../../models/propiedad';

@Component({
  selector: 'app-visitas-recibidas',
  standalone: false,
  templateUrl: './visitas-recibidas.html',
  styleUrl: './visitas-recibidas.css',
})
export class VisitasRecibidas {
  errorCarga = '';

  listaVisitas: Visita[] = [];
  cargando: boolean = true;

  constructor(
    private propiedadService: PropiedadService,
    private visitaService: VisitaService,
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
          this.listaVisitas = [];
          this.cargando = false;
          this.cdr.detectChanges();
          return;
        }

        const llamadas = propiedades.map(p =>
          this.visitaService.listByPropiedadId(p.id)
        );

        forkJoin(llamadas).subscribe({
          next: (resultados) => {
            this.listaVisitas = resultados.flat().sort((a, b) => a.fecha.localeCompare(b.fecha));
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

  YaOcurrio(fecha: string) { return new Date(fecha) <= new Date(); }

  Confirmar(id: number) {
    this.visitaService.actualizarEstado(id, 'COMPLETADA').subscribe({
      next: () => {
        this.snackBar.open('Visita completada', '', { duration: 1500 });
        this.CargarLista();
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open('No se pudo confirmar la visita', '', { duration: 2500 });
      }
    });
  }

  Cancelar(id: number) {
    this.visitaService.actualizarEstado(id, 'CANCELADA').subscribe({
      next: () => {
        this.snackBar.open('Visita cancelada', '', { duration: 1500 });
        this.CargarLista();
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open('No se pudo cancelar la visita', '', { duration: 2500 });
      }
    });
  }
}
