import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Propiedad } from '../../models/propiedad';
import { PropiedadService } from '../../services/propiedad-service';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  errorCarga = '';

  listaPropiedades: Propiedad[] = [];
  cargando: boolean = true;
  filtroForm!: FormGroup;

  constructor(
    private propiedadService: PropiedadService,
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    this.filtroForm = this.formBuilder.group({
      distrito: [''],
      precioMax: [''],
      capacidad: [''],
    });
    this.cargarTodas();
  }

  cargarTodas() {
    this.cargando = true;
    this.propiedadService.listAll().subscribe({
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

  Buscar() {
    const distrito = this.filtroForm.get('distrito')?.value || undefined;
    const precioMax = this.filtroForm.get('precioMax')?.value || undefined;
    const capacidad = this.filtroForm.get('capacidad')?.value || undefined;

    this.cargando = true;
    this.propiedadService.buscarConFiltros(distrito, undefined, precioMax, capacidad).subscribe({
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

  LimpiarFiltros() {
    this.filtroForm.reset();
    this.cargarTodas();
  }
}
