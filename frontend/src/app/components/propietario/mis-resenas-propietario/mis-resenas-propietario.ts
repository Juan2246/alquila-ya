import { Resena } from '../../../models/lecturas';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoin } from 'rxjs';
import { PropiedadService } from '../../../services/propiedad-service';
import { ResenaService } from '../../../services/resena-service';
import { UserService } from '../../../services/user-service';
import { Propiedad } from '../../../models/propiedad';

@Component({
  selector: 'app-mis-resenas-propietario',
  standalone: false,
  templateUrl: './mis-resenas-propietario.html',
  styleUrl: './mis-resenas-propietario.css',
})
export class MisResenasPropietario {
  errorCarga = '';

  listaResenas: Resena[] = [];
  cargando: boolean = true;
  respuestas: { [id: number]: string } = {};
  enviando: { [id: number]: boolean } = {};

  constructor(
    private propiedadService: PropiedadService,
    private resenaService: ResenaService,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { this.cargando = false; return; }

    this.cargando = true;
    this.propiedadService.listByPropietarioId(parseInt(perfilId)).subscribe({
      next: (propiedades: Propiedad[]) => {
        if (propiedades.length === 0) {
          this.listaResenas = [];
          this.cargando = false;
          this.cdr.detectChanges();
          return;
        }

        const llamadas = propiedades.map(p =>
          this.resenaService.listByPropiedadId(p.id)
        );

        forkJoin(llamadas).subscribe({
          next: (resultados) => {
            this.listaResenas = resultados.flat();
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

  Responder(resena: Resena) {
    const texto = this.respuestas[resena.id];
    if (!texto || !texto.trim()) {
      this.snackBar.open('Escribe una respuesta antes de enviar', '', { duration: 2000 });
      return;
    }

    this.enviando[resena.id] = true;
    this.resenaService.responder(resena.id, texto).subscribe({
      next: (actualizada) => {
        resena.respuestaPropietario = actualizada.respuestaPropietario;
        this.enviando[resena.id] = false;
        this.snackBar.open('Respuesta publicada', '', { duration: 1500 });
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.enviando[resena.id] = false;
        this.snackBar.open('No se pudo publicar la respuesta', '', { duration: 2500 });
      }
    });
  }
}
