import { concat, defer, of, Observable } from 'rxjs';
import { concatMap, tap, toArray, finalize } from 'rxjs/operators';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { archivoUrl } from '../../../shared/archivo-url';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { PropiedadService } from '../../../services/propiedad-service';
import { ComodidadService } from '../../../services/comodidad-service';
import { UserService } from '../../../services/user-service';
import { Comodidad } from '../../../models/comodidad';
import { Propiedad } from '../../../models/propiedad';
import { PropiedadSolicitud } from '../../../models/propiedadDTO';
import { PropiedadClausula } from '../../../models/propiedadClausula';

@Component({
  selector: 'app-publicar-propiedad',
  standalone: false,
  templateUrl: './publicar-propiedad.html',
  styleUrl: './publicar-propiedad.css',
})
export class PublicarPropiedad {
  errorCarga = '';

  id: number = 0;
  publicarForm!: FormGroup;
  listaComodidades: Comodidad[] = [];
  comodidadesSeleccionadas: Set<number> = new Set();
  guardando: boolean = false;

  private fotoSubidaUrl: string | null = null;
  archivoSeleccionado: File | null = null;
  previsualizacionUrl: string | null = null;
  fotoActualUrl: string | null = null;

  clausulasExistentes: PropiedadClausula[] = [];
  clausulasNuevas: string[] = [];
  nuevaClausulaTexto: string = '';

  constructor(
    private propiedadService: PropiedadService,
    private comodidadService: ComodidadService,
    private userService: UserService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    this.publicarForm = this.formBuilder.group({
      titulo: ['', Validators.required],
      descripcion: ['', Validators.required],
      ubicacion: ['', Validators.required],
      distrito: ['', Validators.required],
      precio: ['', [Validators.required, Validators.min(1)]],
      habitaciones: ['', [Validators.required, Validators.min(1)]],
      capacidad: ['', [Validators.required, Validators.min(1)]],
    });

    this.comodidadService.listAll().subscribe({
      next: (data: Comodidad[]) => { this.listaComodidades = data; },
      error: (err) => { this.errorCarga = 'No se pudo cargar esta información. Reintenta la consulta.'; },
    });

    this.id = parseInt(this.activatedRoute.snapshot.params['id']);
    if (this.id > 0 && !isNaN(this.id)) {
      this.propiedadService.getById(this.id).subscribe({
        next: (data: Propiedad) => {
          this.publicarForm.patchValue({
            titulo: data.titulo,
            descripcion: data.descripcion,
            ubicacion: data.ubicacion,
            distrito: data.distrito,
            precio: data.precio,
            habitaciones: data.habitaciones,
            capacidad: data.capacidad,
          });
          (data.comodidades || []).forEach(c => this.comodidadesSeleccionadas.add(c.id));
          this.clausulasExistentes = data.clausulas || [];
          if (data.fotos && data.fotos.length > 0) {
            this.fotoActualUrl = archivoUrl(data.fotos[0].url);
          }
        },
        error: (err) => { this.errorCarga = 'No se pudo cargar esta información. Reintenta la consulta.'; },
      });
    } else {
      this.id = 0;
    }
  }

  ToggleComodidad(id: number, event: MatCheckboxChange) {
    if (event.checked) {
      this.comodidadesSeleccionadas.add(id);
    } else {
      this.comodidadesSeleccionadas.delete(id);
    }
  }

  SeleccionarArchivo(event: Event) {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    if (!archivo) { return; }
    if (!['image/png', 'image/jpeg'].includes(archivo.type) || archivo.size > 10 * 1024 * 1024) {
      this.snackBar.open('Selecciona una imagen PNG o JPEG de hasta 10 MB.', 'Cerrar', { duration: 3500 }); return;
    }
    this.fotoSubidaUrl = null;
    this.archivoSeleccionado = archivo;

    const lector = new FileReader();
    lector.onload = () => { this.previsualizacionUrl = lector.result as string; };
    lector.readAsDataURL(archivo);
  }

  AgregarClausula() {
    if (this.guardando) { return; }
    const texto = this.nuevaClausulaTexto.trim();
    if (!texto || texto.length > 250) { return; }

    if (this.id > 0) {
      // Propiedad ya existe (modo edición): guardamos la cláusula de inmediato
      this.propiedadService.agregarClausula(this.id, texto).subscribe({
        next: (nueva: PropiedadClausula) => {
          this.clausulasExistentes.push(nueva);
          this.nuevaClausulaTexto = '';
        },
        error: (err) => {
          this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
          this.snackBar.open('No se pudo agregar la regla', '', { duration: 2500 });
        }
      });
    } else {
      // Propiedad nueva: se guarda localmente y se envía recién al publicar
      this.clausulasNuevas.push(texto);
      this.nuevaClausulaTexto = '';
    }
  }

  EliminarClausulaExistente(clausula: PropiedadClausula) {
    this.propiedadService.eliminarClausula(this.id, clausula.id).subscribe({
      next: () => {
        this.clausulasExistentes = this.clausulasExistentes.filter(c => c.id !== clausula.id);
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open('No se pudo eliminar la regla', '', { duration: 2500 });
      }
    });
  }

  EliminarClausulaNueva(index: number) {
    this.clausulasNuevas.splice(index, 1);
  }

  Guardar() {
    if (this.guardando) { return; }
    this.publicarForm.markAllAsTouched();
    if (this.publicarForm.invalid) { return; }
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { return; }
    const solicitud: PropiedadSolicitud = {
      ...this.publicarForm.getRawValue(),
      latitud: 0, longitud: 0, propietarioId: Number(perfilId),
      comodidadIds: Array.from(this.comodidadesSeleccionadas),
    };
    this.guardando = true;
    this.errorCarga = '';
    const guardar: Observable<{ id: number }> = this.id > 0
      ? this.propiedadService.update({ ...solicitud, id: this.id })
      : this.propiedadService.add(solicitud);
    guardar.pipe(
      tap(propiedad => { this.id = propiedad.id; }),
      concatMap(() => {
        const foto = !this.archivoSeleccionado ? of(null) :
          (this.fotoSubidaUrl ? of({ url: this.fotoSubidaUrl }) : this.propiedadService.subirFoto(this.archivoSeleccionado)).pipe(
            tap(resultado => { this.fotoSubidaUrl = resultado.url; }),
            concatMap(resultado => this.propiedadService.agregarFoto(this.id, resultado.url)),
            tap(() => { this.archivoSeleccionado = null; this.fotoSubidaUrl = null; })
          );
        const reglas = [...this.clausulasNuevas].map(texto => defer(() => this.propiedadService.agregarClausula(this.id, texto)).pipe(
          tap(regla => { this.clausulasExistentes.push(regla); this.clausulasNuevas.shift(); })
        ));
        return concat(foto, ...reglas).pipe(toArray());
      }),
      finalize(() => { this.guardando = false; })
    ).subscribe({
      next: () => {
        this.snackBar.open('Propiedad, fotos y reglas guardadas.', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/mis-propiedades']);
      },
      error: () => {
        this.errorCarga = this.id > 0
          ? 'La propiedad se guardó, pero quedan cambios pendientes. Pulsa Guardar para reintentarlos.'
          : 'No se pudo guardar la propiedad. Revisa la conexión e inténtalo otra vez.';
      }
    });
  }
}
