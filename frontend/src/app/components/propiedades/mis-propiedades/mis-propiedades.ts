import { ChangeDetectorRef, Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Propiedad } from '../../../models/propiedad';
import { PropiedadService } from '../../../services/propiedad-service';
import { UserService } from '../../../services/user-service';
import { ConfirmacionEliminar } from '../../confirmaciones/confirmacion-eliminar/confirmacion-eliminar';

@Component({
  selector: 'app-mis-propiedades',
  standalone: false,
  templateUrl: './mis-propiedades.html',
  styleUrl: './mis-propiedades.css',
})
export class MisPropiedades {
  errorCarga = '';

  listaPropiedades: Propiedad[] = [];
  cargando: boolean = true;

  constructor(
    private propiedadService: PropiedadService,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  Editar(id: number) {
    this.router.navigate(['/publicar-propiedad', id]);
  }

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
      next: (data: Propiedad[]) => {
        this.listaPropiedades = data;
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

  Eliminar(id: number) {
    const dialogRef = this.dialog.open(ConfirmacionEliminar);
    dialogRef.afterClosed().subscribe(accionSeleccionada => {
      if (accionSeleccionada) {
        this.propiedadService.delete(id).subscribe({
          next: () => {
            this.snackBar.open('Propiedad eliminada', '', { duration: 1500 });
            this.CargarLista();
          },
          error: (err) => {
            this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
            this.snackBar.open('No se pudo eliminar la propiedad', '', { duration: 2500 });
          }
        });
      }
    });
  }
}
