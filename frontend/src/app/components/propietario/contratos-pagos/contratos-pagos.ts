import { PagoDTO } from '../../../models/pagoDTO';
import { Contrato } from '../../../models/lecturas';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoin, finalize } from 'rxjs';
import { PropiedadService } from '../../../services/propiedad-service';
import { ContratoService } from '../../../services/contrato-service';
import { PagoService } from '../../../services/pago-service';
import { Propiedad } from '../../../models/propiedad';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-contratos-pagos',
  standalone: false,
  templateUrl: './contratos-pagos.html',
  styleUrl: './contratos-pagos.css',
})
export class ContratosPagos {
  errorCarga = '';

  listaContratos: Contrato[] = [];
  cargando: boolean = true;
  contratoExpandidoId: number | null = null;
  pagosPorContrato: { [id: number]: PagoDTO[] } = {};
  pagoForm!: FormGroup;
  guardandoPago = false;
  cargandoPagos = false;
  errorPagos = false;

  constructor(
    private propiedadService: PropiedadService,
    private contratoService: ContratoService,
    private pagoService: PagoService,
    private userService: UserService,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    this.pagoForm = this.formBuilder.group({
      monto: ['', [Validators.required, Validators.min(1)]],
      metodo: ['Transferencia', Validators.required],
    });
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
          this.listaContratos = [];
          this.cargando = false;
          this.cdr.detectChanges();
          return;
        }

        const llamadas = propiedades.map(p =>
          this.contratoService.listByPropiedadId(p.id)
        );

        forkJoin(llamadas).subscribe({
          next: (resultados) => {
            this.listaContratos = resultados.flat();
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

  VerPagos(contratoId: number) {
    if (this.contratoExpandidoId === contratoId) {
      this.contratoExpandidoId = null;
      return;
    }
    this.contratoExpandidoId = contratoId;
    this.pagoForm.reset({ monto: '', metodo: 'Transferencia' });

    this.CargarPagos(contratoId);
  }

  private CargarPagos(contratoId: number) {
    this.cargandoPagos = true; this.errorPagos = false;
    this.pagoService.listByContratoId(contratoId).pipe(finalize(() => { this.cargandoPagos = false; })).subscribe({
      next: pagos => { this.pagosPorContrato[contratoId] = pagos; this.cdr.detectChanges(); },
      error: () => { this.errorPagos = true; this.cdr.detectChanges(); }
    });
  }

  RegistrarPago(contratoId: number) {
    if (this.pagoForm.invalid || this.guardandoPago || this.listaContratos.find(c => c.id === contratoId)?.estado !== 'FIRMADO') { return; }
    this.guardandoPago = true;
    this.pagoService.add({ monto: Number(this.pagoForm.get('monto')?.value), metodo: this.pagoForm.get('metodo')?.value, contratoId }).pipe(
      finalize(() => { this.guardandoPago = false; })
    ).subscribe({
      next: () => {
        this.snackBar.open('Pago recibido registrado', 'Cerrar', { duration: 2500 });
        this.pagoForm.reset({ monto: '', metodo: 'Transferencia' }); this.CargarPagos(contratoId);
      },
      error: () => { this.snackBar.open('No se pudo registrar el pago. Revisa el importe y el estado del contrato.', 'Cerrar', { duration: 4500 }); }
    });
  }
}
