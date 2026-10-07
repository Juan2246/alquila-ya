import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { archivoUrl } from '../../../shared/archivo-url';
import { Resena } from '../../../models/lecturas';
import { ChangeDetectorRef, Component, DestroyRef, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Propiedad } from '../../../models/propiedad';
import { CotizacionDTO } from '../../../models/cotizacionDTO';
import { PropiedadService } from '../../../services/propiedad-service';
import { ReservaService } from '../../../services/reserva-service';
import { ResenaService } from '../../../services/resena-service';
import { VisitaService } from '../../../services/visita-service';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-detalle-propiedad',
  standalone: false,
  templateUrl: './detalle-propiedad.html',
  styleUrl: './detalle-propiedad.css',
})
export class DetallePropiedad {
  errorCarga = '';

  private readonly destroyRef = inject(DestroyRef);
  private versionFechas = 0;
  propiedad!: Propiedad;
  resenas: Resena[] = [];
  cargando: boolean = true;

  reservaForm!: FormGroup;
  resenaForm!: FormGroup;

  cotizacion: CotizacionDTO | null = null;
  cotizando: boolean = false;
  reservando: boolean = false;
  agendandoVisita: boolean = false;
  fechaVisita: string = '';

  imagenPorDefecto: string = '/sin-foto.svg';

  get imagenPrincipal(): string {
    if (this.propiedad?.fotos && this.propiedad.fotos.length > 0) {
      return archivoUrl(this.propiedad.fotos[0].url);
    }
    return this.imagenPorDefecto;
  }

  constructor(
    private activatedRoute: ActivatedRoute,
    private propiedadService: PropiedadService,
    private reservaService: ReservaService,
    private resenaService: ResenaService,
    private visitaService: VisitaService,
    public userService: UserService,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    const id = parseInt(this.activatedRoute.snapshot.params['id']);

    this.reservaForm = this.formBuilder.group({
      checkIn: ['', Validators.required],
      checkOut: ['', Validators.required],
    });

    this.reservaForm.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.cotizacion = null; this.cotizando = false; this.versionFechas++;
    });

    this.resenaForm = this.formBuilder.group({
      puntuacionLimpieza: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      puntuacionUbicacion: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      puntuacionComunicacion: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comentario: ['', Validators.required],
    });

    this.propiedadService.getById(id).subscribe({
      next: (data: Propiedad) => {
        this.propiedad = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });

    this.resenaService.listByPropiedadId(id).subscribe({
      next: (data) => { this.resenas = data; this.cdr.detectChanges(); },
      error: (err) => { this.errorCarga = 'No se pudo cargar esta información. Reintenta la consulta.'; },
    });
  }

  private fechasValidas(): boolean {
    this.reservaForm.markAllAsTouched();
    const { checkIn, checkOut } = this.reservaForm.getRawValue();
    const hoy = new Date(); hoy.setHours(0, 0, 0, 0);
    if (this.reservaForm.invalid || !checkIn || !checkOut || checkOut <= checkIn || new Date(checkIn + 'T00:00:00') < hoy) {
      this.snackBar.open('Elige fechas válidas: entrada desde hoy y salida posterior.', 'Cerrar', { duration: 3500 });
      return false;
    }
    return true;
  }

  Cotizar() {
    if (!this.fechasValidas()) { return; }
    const version = this.versionFechas;
    const checkIn = this.reservaForm.get('checkIn')?.value;
    const checkOut = this.reservaForm.get('checkOut')?.value;

    this.cotizando = true;
    this.propiedadService.cotizar(this.propiedad.id, checkIn, checkOut).subscribe({
      next: (data: CotizacionDTO) => {
        if (version !== this.versionFechas) { return; }
        this.cotizacion = data;
        this.cotizando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        if (version !== this.versionFechas) { return; }
        this.cotizando = false;
        this.cotizacion = null;
        this.snackBar.open(err?.error?.message || 'No se pudo calcular la cotización', '', { duration: 3000 });
      }
    });
  }

  Reservar() {
    if (this.reservando || !this.fechasValidas()) { return; }
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) {
      this.snackBar.open('No se encontró tu perfil de huésped', '', { duration: 2500 });
      return;
    }

    const checkIn = this.reservaForm.get('checkIn')?.value;
    const checkOut = this.reservaForm.get('checkOut')?.value;

    this.reservando = true;
    this.reservaService.add({
      fechaCheckIn: checkIn,
      fechaCheckOut: checkOut,
      clienteId: parseInt(perfilId),
      propiedadId: this.propiedad.id,
    }).subscribe({
      next: (reserva) => {
        this.reservando = false;
        this.snackBar.open('Reserva solicitada por S/ ' + reserva.precioTotal + '. Espera la confirmación del anfitrión.', 'Cerrar', { duration: 5000 });
        this.router.navigate(['/mis-contratos']);
      },
      error: (err) => {
        this.reservando = false;
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open(err?.error?.message || 'No se pudo crear la reserva', '', { duration: 3000 });
      }
    });
  }

  AgendarVisita() {
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) {
      this.snackBar.open('No se encontró tu perfil de huésped', '', { duration: 2500 });
      return;
    }
    if (!this.fechaVisita) {
      this.snackBar.open('Elige una fecha para la visita', '', { duration: 2000 });
      return;
    }

    this.agendandoVisita = true;
    this.visitaService.add({
      fecha: this.fechaVisita,
      clienteId: parseInt(perfilId),
      propiedadId: this.propiedad.id,
    }).subscribe({
      next: () => {
        this.agendandoVisita = false;
        this.fechaVisita = '';
        this.snackBar.open('¡Visita agendada! Consulta su estado en Mis reservas', '', { duration: 3000 });
      },
      error: (err) => {
        this.agendandoVisita = false;
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open(err?.error?.message || 'No se pudo agendar la visita', '', { duration: 3000 });
      }
    });
  }

  DejarResena() {
    if (this.resenaForm.invalid) { return; }
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { return; }

    this.resenaService.add({
      ...this.resenaForm.value,
      clienteId: parseInt(perfilId),
      propiedadId: this.propiedad.id,
    }).subscribe({
      next: (data) => {
        this.resenas.unshift(data);
        this.resenaForm.reset({ puntuacionLimpieza: 5, puntuacionUbicacion: 5, puntuacionComunicacion: 5, comentario: '' });
        this.snackBar.open('¡Gracias por tu resena!', '', { duration: 2000 });
      },
      error: (err) => {
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open(err?.error?.message || 'No se pudo publicar la resena', '', { duration: 3000 });
      }
    });
  }
}
