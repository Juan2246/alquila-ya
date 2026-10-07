import { Contrato } from '../../../models/lecturas';
import { ChangeDetectorRef, Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ContratoService } from '../../../services/contrato-service';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-firmar-contrato',
  standalone: false,
  templateUrl: './firmar-contrato.html',
  styleUrl: './firmar-contrato.css',
})
export class FirmarContrato {
  errorCarga = '';

  firmaUrl: string | null = null;

  contratoId: number = 0;
  contrato: Contrato | null = null;
  cargando: boolean = true;
  firmando: boolean = false;

  private lienzoFirmaRef?: ElementRef<HTMLCanvasElement>;
  private ctx?: CanvasRenderingContext2D;
  private dibujando = false;
  private tieneTrazo = false;

  // Se usa un setter en vez de AfterViewInit porque el <canvas> está dentro
  // de un *ngIf que solo aparece despues de cargar los datos del contrato;
  // con un setter, Angular llama de nuevo apenas el elemento existe en el DOM.
  @ViewChild('lienzoFirma') set lienzoFirma(ref: ElementRef<HTMLCanvasElement> | undefined) {
    if (!ref) { return; }
    this.lienzoFirmaRef = ref;
    const canvas = ref.nativeElement;
    this.ctx = canvas.getContext('2d')!;
    this.ctx.lineWidth = 2.5;
    this.ctx.lineCap = 'round';
    this.ctx.strokeStyle = '#000000';
  }

  constructor(
    private activatedRoute: ActivatedRoute,
    private contratoService: ContratoService,
    public userService: UserService,
    private snackBar: MatSnackBar,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    this.contratoId = parseInt(this.activatedRoute.snapshot.params['id']);
    this.contratoService.getById(this.contratoId).subscribe({
      next: (data) => {
        this.contrato = data;
        if (data.firmaImagenUrl) {
          this.contratoService.leerFirma(data.id).subscribe({
            next: blob => { this.firmaUrl = URL.createObjectURL(blob); this.cdr.detectChanges(); },
            error: () => { this.errorCarga = 'No se pudo cargar la firma privada.'; this.cdr.detectChanges(); }
          });
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
  }

  private obtenerPosicion(event: MouseEvent | TouchEvent, canvas: HTMLCanvasElement) {
    const rect = canvas.getBoundingClientRect();
    if ('touches' in event) {
      const touch = event.touches[0] || event.changedTouches[0];
      return { x: (touch.clientX - rect.left) * canvas.width / rect.width, y: (touch.clientY - rect.top) * canvas.height / rect.height };
    }
    return { x: (event.clientX - rect.left) * canvas.width / rect.width, y: (event.clientY - rect.top) * canvas.height / rect.height };
  }

  IniciarTrazo(event: MouseEvent | TouchEvent) {
    if (!this.ctx || !this.lienzoFirmaRef) { return; }
    event.preventDefault();
    const canvas = this.lienzoFirmaRef.nativeElement;
    const pos = this.obtenerPosicion(event, canvas);
    this.dibujando = true;
    this.tieneTrazo = true;
    this.ctx.beginPath();
    this.ctx.moveTo(pos.x, pos.y);
  }

  Dibujar(event: MouseEvent | TouchEvent) {
    if (!this.dibujando || !this.ctx || !this.lienzoFirmaRef) { return; }
    event.preventDefault();
    const canvas = this.lienzoFirmaRef.nativeElement;
    const pos = this.obtenerPosicion(event, canvas);
    this.ctx.lineTo(pos.x, pos.y);
    this.ctx.stroke();
  }

  TerminarTrazo() {
    this.dibujando = false;
  }

  Limpiar() {
    if (!this.ctx || !this.lienzoFirmaRef) { return; }
    const canvas = this.lienzoFirmaRef.nativeElement;
    this.ctx.clearRect(0, 0, canvas.width, canvas.height);
    this.tieneTrazo = false;
  }

  ngOnDestroy() { if (this.firmaUrl) { URL.revokeObjectURL(this.firmaUrl); } }

  ConfirmarFirma() {
    if (this.firmando || this.contrato?.estado !== 'ACTIVO' || !this.userService.esCliente()) { return; }
    if (!this.tieneTrazo || !this.lienzoFirmaRef) {
      this.snackBar.open('Dibuja tu firma antes de continuar', '', { duration: 2000 });
      return;
    }

    const canvas = this.lienzoFirmaRef.nativeElement;
    this.firmando = true;
    canvas.toBlob((blob) => {
      if (!blob) { this.firmando = false; return; }
      this.contratoService.firmar(this.contratoId, blob).subscribe({
        next: () => {
          this.firmando = false;
          this.snackBar.open('¡Contrato firmado con éxito!', '', { duration: 2500 });
          this.router.navigate(['/mis-reservas']);
        },
        error: (err) => {
          this.firmando = false;
          this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
          this.snackBar.open('No se pudo guardar la firma', '', { duration: 3000 });
        }
      });
    }, 'image/png');
  }
}
