import { Contrato } from '../../../models/lecturas';
import { ChangeDetectorRef, Component } from '@angular/core';
import { Router } from '@angular/router';
import { ContratoService } from '../../../services/contrato-service';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-mis-contratos',
  standalone: false,
  templateUrl: './mis-contratos.html',
  styleUrl: './mis-contratos.css',
})
export class MisContratos {
  errorCarga = '';

  listaContratos: Contrato[] = [];
  cargando: boolean = true;

  constructor(
    private contratoService: ContratoService,
    private userService: UserService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    const perfilId = this.userService.getPerfilIdLogeado();
    if (!perfilId) { this.cargando = false; return; }

    this.contratoService.listByClienteId(parseInt(perfilId)).subscribe({
      next: (data) => {
        this.listaContratos = data;
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

  Firmar(id: number) {
    this.router.navigate(['/contratos', id, 'firmar']);
  }
}
